package ifmo.service;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.MessageDTO;
import ifmo.dto.UserEntityDto;
import ifmo.exceptions.CustomExistsException;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.feign_client.ChatUserClient;
import ifmo.feign_client.UserClient;
import ifmo.model.ChatEntity;
import ifmo.model.MessageEntity;
import ifmo.repository.ChatRepository;
import ifmo.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ChatUserClient chatUserClient;

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
        var firstUser = userClient.getUser(firstLogin);
        var secondUser = userClient.getUser(secondLogin);

        var existingChats = chatUserClient.getAllChatsByUser(firstLogin).getBody();

        var found = existingChats.stream()
                .filter(chatDTO -> Objects.requireNonNull(chatUserClient.getAllUsersByChat(chatDTO.getId())
                                .getBody())
                        .contains(new UserEntityDto(1L, firstLogin)) &&
                        Objects.requireNonNull(chatUserClient.getAllUsersByChat(chatDTO.getId())
                                        .getBody())
                                .contains(new UserEntityDto(1L, secondLogin)))
                .findFirst();
        if (found.isPresent()) throw new CustomExistsException("Чат между пользователями уже существует");

        ChatEntity chatEntity = new ChatEntity();
        //TODO: call save in userchat
//        chatEntity.getUsers().add(firstUser);
//        chatEntity.getUsers().add(secondUser);
        chatEntity = chatRepository.save(chatEntity);

        MessageEntity newMessage = new MessageEntity();
        newMessage.setContent(message);
        newMessage.setSender(Objects.requireNonNull(firstUser.getBody()).getId());
        newMessage.setChat(chatEntity);
        messageRepository.save(newMessage);

        return new ChatEntityDto(chatEntity);
    }
}
