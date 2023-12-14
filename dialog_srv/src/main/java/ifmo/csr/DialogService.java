package ifmo.csr;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.UserEntityDto;
import ifmo.exceptions.CustomInternalException;
import ifmo.feign_client.ChatClient;
import ifmo.feign_client.UserClient;
import ifmo.model.DialogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogService {
    private final DialogRepository dialogRepository;
    private final ChatClient chatClient;
    private final UserClient userClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public List<ChatEntityDto> getAllChatsByUserLogin(String userLogin) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var user = breaker.run(() -> userClient.getUser(userLogin), throwable -> userClient.getUserFallback());
        if (user.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");
        return dialogRepository.getChatUserEntitiesByUserId(Objects.requireNonNull(user.getBody()).id())
                .stream()
                .map(chatUser -> chatClient.getChat(chatUser.getChatId()).getBody())
                .collect(Collectors.toList());
    }

    public List<UserEntityDto> getAllUsersByChat(Long chatId) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var chat = breaker.run(() -> chatClient.getChat(chatId), throwable -> chatClient.getChatFallback());
        if (chat.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");

        return dialogRepository.getChatUserEntitiesByChatId(Objects.requireNonNull(chat.getBody()).getId())
                .stream()
                .map(chatUser -> userClient.getUserById(chatUser.getUserId()).getBody())
                .collect(Collectors.toList());
    }

    public void saveChatUser(Long chatId, Long userId) {
        DialogEntity entity = new DialogEntity();
        entity.setChatId(chatId);
        entity.setUserId(userId);
        dialogRepository.save(entity);
    }

}
