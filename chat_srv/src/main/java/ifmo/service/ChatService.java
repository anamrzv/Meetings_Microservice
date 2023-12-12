package ifmo.service;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.DialogEntityDto;
import ifmo.dto.MessageDTO;
import ifmo.dto.UserEntityDto;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserClient userClient;
    private final DialogClient dialogClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public Page<MessageDTO> getAllMessagesByChatId(Long id, Pageable pageable) {
        var chat = chatRepository.getChatEntityById(id).orElseThrow(() -> new CustomNotFoundException("Чата с таким id не существует"));
        return messageRepository.findAllByChat(chat, pageable).map(MessageDTO::new);
    }

    public ChatEntityDto getChatById(Long id) {
        var chat = chatRepository.getChatEntityById(id).orElseThrow(() -> new CustomNotFoundException("Чат не найден"));
        return new ChatEntityDto(chat);
    }

    @Transactional
    public MessageDTO addMessageToChat(Long chatId, long senderId, String message) {
        try {
            var chat = chatRepository.getReferenceById(chatId);
            MessageEntity newMessage = new MessageEntity();
            newMessage.setContent(message);
            newMessage.setSender(senderId);
            newMessage.setChat(chat);
            var savedMessage = messageRepository.save(newMessage);
            return new MessageDTO(savedMessage);
        } catch (EntityNotFoundException e) {
            throw new CustomNotFoundException("Чат пользователя не найден");
        }
    }

    @Transactional
    public ChatEntityDto createChat(String firstLogin, String secondLogin, String message) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var firstUserResponse = breaker.run(() -> userClient.getUser(firstLogin), throwable -> userClient.getUserFallback());
        var secondUserResponse = breaker.run(() -> userClient.getUser(secondLogin), throwable -> userClient.getUserFallback());
        var chatResponse = breaker.run(() -> dialogClient.getAllChatsByUser(firstLogin), throwable -> dialogClient.getAllChatsByUserFallback());
        if (firstUserResponse.getStatusCode().is5xxServerError() || secondUserResponse.getStatusCode().is5xxServerError() || chatResponse.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");

        var firstUser = firstUserResponse.getBody();
        var secondUser = secondUserResponse.getBody();
        var existingChats = chatResponse.getBody();

        if (existingChats == null) throw new CustomNotFoundException("Ошибка при создании чата");
        var found = existingChats.stream()
                .filter(chatDTO -> Objects.requireNonNull(dialogClient.getAllUsersByChat(chatDTO.getId())
                                .getBody())
                        .contains(new UserEntityDto(1L, firstLogin)) &&
                        Objects.requireNonNull(dialogClient.getAllUsersByChat(chatDTO.getId())
                                        .getBody())
                                .contains(new UserEntityDto(1L, secondLogin)))
                .findFirst();
        if (found.isPresent()) throw new CustomExistsException("Чат между пользователями уже существует");

        var chatEntity = chatRepository.save(new ChatEntity());
        dialogClient.saveChatUser(new DialogEntityDto(chatEntity.getId(), Objects.requireNonNull(firstUser).getId()));
        dialogClient.saveChatUser(new DialogEntityDto(chatEntity.getId(), Objects.requireNonNull(secondUser).getId()));

        MessageEntity newMessage = new MessageEntity();
        newMessage.setContent(message);
        newMessage.setSender(Objects.requireNonNull(firstUser).getId());
        newMessage.setChat(chatEntity);
        messageRepository.save(newMessage);

        return new ChatEntityDto(chatEntity);
    }
}
