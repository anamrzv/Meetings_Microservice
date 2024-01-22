package ifmo.csr;

import  ifmo.dto.ChatEntityDto;
import ifmo.dto.UserEntityDto;
import ifmo.dto.UtilDto;
import ifmo.exceptions.CustomInternalException;
import ifmo.feign_client.ChatClient;
import ifmo.feign_client.UserClient;
import ifmo.model.DialogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableRabbit
public class DialogService {
    private final DialogRepository dialogRepository;
    private final ChatClient chatClient;
    private final UserClient userClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    private static final String chatQueue = "chat-queue";
    private static final String saveQueue = "save-queue";
    private static final String userQueue = "user-queue";

    @RabbitListener(queues = chatQueue, returnExceptions = "true")
    public List<ChatEntityDto> getAllChatsByUserLogin(UtilDto utilDto) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var user = breaker.run(() -> userClient.getUser(utilDto.getUserLogin(), utilDto.getToken()), throwable -> userClient.getUserFallback());
        if (user.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");
        var dialogs = dialogRepository.getChatUserEntitiesByUserId(Objects.requireNonNull(user.getBody()).id());
        List<ChatEntityDto> resultChats = new LinkedList<>();
        for (DialogEntity dialog: dialogs) {
            ChatEntityDto chat = (ChatEntityDto) breaker.run(() -> chatClient.getChat(dialog.getChatId(), utilDto.getToken()).getBody(), throwable -> chatClient.getChatFallback());
            resultChats.add(chat);
        }
        return resultChats;
    }

    @RabbitListener(queues = userQueue, returnExceptions = "true")
    public List<UserEntityDto> getAllUsersByChat(UtilDto utilDto) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var chat = breaker.run(() -> chatClient.getChat(utilDto.getChatId(), utilDto.getToken()), throwable -> chatClient.getChatFallback());
        if (chat.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");
        return dialogRepository.getChatUserEntitiesByChatId(Objects.requireNonNull(chat.getBody()).getId())
                .stream()
                .map(chatUser -> userClient.getUserById(chatUser.getUserId(), utilDto.getToken()).getBody())
                .collect(Collectors.toList());
    }

    @RabbitListener(queues = saveQueue, returnExceptions = "true")
    public boolean saveChatUser(UtilDto utilDto) {
        DialogEntity entity = new DialogEntity();
        entity.setChatId(utilDto.getChatId());
        entity.setUserId(utilDto.getUserId());
        dialogRepository.save(entity);
        return true;
    }

}
