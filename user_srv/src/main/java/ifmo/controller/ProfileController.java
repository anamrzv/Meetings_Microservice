package ifmo.controller;

import ifmo.dto.IconRequest;
import ifmo.dto.ProfileEntityDto;
import ifmo.exceptions.CustomInternalException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String showProfileKey = "show-profile";
    private static final String updateProfileKey = "update-profile";
    private static final String uploadIconKey = "upload-icon";
    private static final String getIconKey = "get-icon";

    @Operation(summary = "Показать данные профиля")
    @GetMapping(value = "/{profile_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProfileEntityDto> showProfileById(@PathVariable(value = "profile_id") @Min(1) long profileId) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, showProfileKey, profileId);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((ProfileEntityDto) answer);

    }

    @Operation(summary = "Обновить профиль")
    @PutMapping(value = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ProfileEntityDto> updateProfile(@Valid @RequestBody ProfileEntityDto changedProfile,
                                                           @RequestHeader("Username") String userLogin) {
        changedProfile.setSecondUser(userLogin);
        var answer = amqpTemplate.convertSendAndReceive(exchanger, updateProfileKey, changedProfile);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((ProfileEntityDto) answer);
    }

    @Operation(summary = "Обновить фотографию профиля")
    @PostMapping("/photo/upload")
    public ResponseEntity<String> uploadIcon(@RequestParam("file") MultipartFile file,
                                             @RequestHeader("Username") String userLogin) {
        try{
            var answer = amqpTemplate.convertSendAndReceive(exchanger, uploadIconKey, new IconRequest(userLogin, file.getBytes()));
            if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
            if (answer.getClass().isInstance(new RemoteInvocationResult())) {
                var a = (RemoteInvocationResult) answer;
                throw (RuntimeException) Objects.requireNonNull(a.getException());
            }
            var success = (Boolean) answer;
            if (success) return ResponseEntity.ok("Фотография профиля обновлена");
            else return ResponseEntity.internalServerError().body("Ошибка при обновлении фотографии профиля");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка при считывании фотографии профиля");
        }
    }

    @Operation(summary = "Просмотреть фотографию профиля")
    @GetMapping("/photo/get")
    public ResponseEntity<byte[]> getIcon(@Param("login") String login) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, getIconKey, login);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        var image = (byte[]) answer;
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

}
