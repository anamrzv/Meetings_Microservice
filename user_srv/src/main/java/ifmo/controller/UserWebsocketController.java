package ifmo.controller;

import ifmo.dto.UserEntityDto;
import ifmo.exceptions.CustomInternalException;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
@AllArgsConstructor
public class UserWebsocketController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String showUserKey = "show-user";
    private static final String updateUserIdKey = "show-user-id";
    @MessageMapping("/getUserByLoginWebsocket")
    @SendTo("/topic/loginResult")
    public UserEntityDto getUserByLoginWebsocket(String login) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, showUserKey, login);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return (UserEntityDto) answer;
    }

    @MessageMapping("/getUserByIdWebsocket")
    @SendTo("/topic/idResult")
    public UserEntityDto getUserById(Long id) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, updateUserIdKey, id);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return (UserEntityDto) answer;

    }
}
