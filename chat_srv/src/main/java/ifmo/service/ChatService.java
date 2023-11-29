package ifmo.service;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.MessageDTO;
import ifmo.exceptions.CustomExistsException;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.model.ChatEntity;
import ifmo.model.MessageEntity;
import ifmo.repository.ChatRepository;
import ifmo.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
//    private final UserRepository userRepository;

    public Page<MessageDTO> getAllMessagesByChatId(Long id, Pageable pageable) {
        var chat = chatRepository.getChatEntityById(id).orElseThrow(() -> new CustomNotFoundException("Чата с таким id не существует"));
        return messageRepository.findAllByChat(chat, pageable).map(MessageDTO::new);
    }

//    public List<ChatEntityDto> getAllChatsByUserLogin(String login) {
//        var user = userRepository.findByLogin(login).orElseThrow(() -> new CustomNotFoundException("Пользователь не найден"));
//        return user.getChats().stream()
//                .map(ChatEntityDto::new)
//                .collect(Collectors.toList());
//    }

//    @Transactional
//    public MessageDTO addMessageToChat(Long chatId, String login, String message) {
//        try {
//            var chat = chatRepository.getReferenceById(chatId);
//            var user = userRepository.findByLogin(login).orElseThrow(() -> new CustomNotFoundException("Пользователь не найден"));
//            MessageEntity newMessage = new MessageEntity();
//            newMessage.setContent(message);
//            newMessage.setSender(user);
//            newMessage.setChat(chat);
//            var savedMessage = messageRepository.save(newMessage);
//            return new MessageDTO(savedMessage);
//        } catch (EntityNotFoundException e) {
//            throw new CustomNotFoundException("Чат пользователя не найден");
//        }
//    }

//    @Transactional
//    public ChatEntityDto createChat(String firstLogin, String secondLogin, String message) {
//        var firstUser = userRepository.findByLogin(firstLogin).orElseThrow(() -> new CustomNotFoundException("Пользователь, который хочет создать чат, не найден"));
//        var secondUser = userRepository.findByLogin(secondLogin).orElseThrow(() -> new CustomNotFoundException("Пользователь, с которым нужно создать чат, не найден"));
//
//        var existingChats = getAllChatsByUserLogin(firstLogin);
//        var found = existingChats.stream()
//                .filter(chatDTO -> chatDTO.getUsers().contains(new UserEntityDto(firstUser)) &&
//                        chatDTO.getUsers().contains(new UserEntityDto(secondUser)))
//                .findFirst();
//
//        if (found.isPresent()) throw new CustomExistsException("Чат между пользователями уже существует");
//
//        ChatEntity chatEntity = new ChatEntity();
//        chatEntity.getUsers().add(firstUser);
//        chatEntity.getUsers().add(secondUser);
//        chatEntity = chatRepository.save(chatEntity);
//
//        firstUser.addChat(chatEntity);
//        secondUser.addChat(chatEntity);
//        userRepository.save(firstUser);
//        userRepository.save(secondUser);
//
//        MessageEntity newMessage = new MessageEntity();
//        newMessage.setContent(message);
//        newMessage.setSender(firstUser);
//        newMessage.setChat(chatEntity);
//        messageRepository.save(newMessage);
//
//        return new ChatEntityDto(chatEntity);
//    }
}
