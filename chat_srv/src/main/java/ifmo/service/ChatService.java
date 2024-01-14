package ifmo.service;

import ifmo.dto.*;
import ifmo.exceptions.CustomExistsException;
import ifmo.exceptions.CustomInternalException;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.feign_client.DialogClient;
import ifmo.feign_client.UserClient;
import ifmo.model.ChatEntity;
import ifmo.model.MessageEntity;
import ifmo.repository.ChatRepository;
import ifmo.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserClient userClient;
    private final DialogClient dialogClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    private static final String findQueue = "find-queue";
    private static final String sendQueue = "send-queue";
    private static final String createQueue = "create-queue";
    private static final String idQueue = "id-queue";

    @RabbitListener(queues = findQueue, returnExceptions = "true")
    public List<MessageDTO> getAllMessagesByChatId(Long id) {
        var chat = chatRepository.getChatEntityById(id).orElseThrow(() -> new CustomNotFoundException("Чата с таким id не существует"));
        return messageRepository.findAllByChat(chat).stream().map(MessageDTO::new).collect(Collectors.toList());
    }

    @RabbitListener(queues = idQueue, returnExceptions = "true")
    public ChatEntityDto getChatById(Long id) {
        var chat = chatRepository.getChatEntityById(id).orElseThrow(() -> new CustomNotFoundException("Чат не найден"));
        return new ChatEntityDto(chat);
    }

    @RabbitListener(queues = sendQueue, returnExceptions = "true")
    @Transactional
    public MessageDTO addMessageToChat(CreateChatDto createChat) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var sender = breaker.run(() -> userClient.getUser(createChat.getUserLogin(), createChat.getHeader()), throwable -> userClient.getUserFallback());
        if (sender.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");

        try {
            var chat = chatRepository.getReferenceById(createChat.getChatId());
            MessageEntity newMessage = new MessageEntity();
            newMessage.setContent(createChat.getMessage());
            newMessage.setSender(Objects.requireNonNull(sender.getBody()).getId());
            newMessage.setChat(chat);
            var savedMessage = messageRepository.save(newMessage);
            return new MessageDTO(savedMessage);
        } catch (EntityNotFoundException e) {
            throw new CustomNotFoundException("Чат пользователя не найден");
        }
    }

    @RabbitListener(queues = createQueue, returnExceptions = "true")
    @Transactional
    public ChatEntityDto createChat(CreateChatDto createChat) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var firstUserResponse = breaker.run(() -> userClient.getUser(createChat.getUserLogin(), createChat.getHeader()), throwable -> userClient.getUserFallback());
        var secondUserResponse = breaker.run(() -> userClient.getUser(createChat.getSecondUserLogin(), createChat.getHeader()), throwable -> userClient.getUserFallback());
        var chatResponse = breaker.run(() -> dialogClient.getAllChatsByUser(createChat.getUserLogin(), createChat.getHeader()), throwable -> dialogClient.getAllChatsByUserFallback());
        if (firstUserResponse.getStatusCode().is5xxServerError() || secondUserResponse.getStatusCode().is5xxServerError() || chatResponse.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");

        var firstUser = firstUserResponse.getBody();
        var secondUser = secondUserResponse.getBody();
        var existingChats = chatResponse.getBody();

        if (existingChats != null) {
            var found = existingChats.stream()
                    .filter(chatDTO -> Objects.requireNonNull(dialogClient.getAllUsersByChat(chatDTO.getId(), createChat.getHeader())
                                    .getBody())
                            .contains(new UserEntityDto(1L, createChat.getUserLogin())) &&
                            Objects.requireNonNull(dialogClient.getAllUsersByChat(chatDTO.getId(), createChat.getHeader())
                                            .getBody())
                                    .contains(new UserEntityDto(1L, createChat.getSecondUserLogin())))
                    .findFirst();
            if (found.isPresent()) throw new CustomExistsException("Чат между пользователями уже существует");
        }

        var chatEntity = chatRepository.save(new ChatEntity());
        dialogClient.saveChatUser(new DialogEntityDto(chatEntity.getId(), Objects.requireNonNull(firstUser).getId()), createChat.getHeader());
        dialogClient.saveChatUser(new DialogEntityDto(chatEntity.getId(), Objects.requireNonNull(secondUser).getId()), createChat.getHeader());

        MessageEntity newMessage = new MessageEntity();
        newMessage.setContent(createChat.getMessage());
        newMessage.setSender(Objects.requireNonNull(firstUser).getId());
        newMessage.setChat(chatEntity);
        messageRepository.save(newMessage);

        var res = chatRepository.getChatEntityById(chatEntity.getId()).get();

        return new ChatEntityDto(res);
    }
}
