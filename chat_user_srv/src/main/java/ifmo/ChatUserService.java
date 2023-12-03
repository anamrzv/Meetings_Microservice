package ifmo;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.UserEntityDto;
import ifmo.feign_client.ChatClient;
import ifmo.feign_client.UserClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository chatUserRepository;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private UserClient userClient;

    public List<ChatEntityDto> getAllChatsByUserLogin(String userLogin) {
        var user = userClient.getUser(userLogin);
        return chatUserRepository.getChatUserEntitiesByUserId(user.getBody().getId())
                .stream()
                .map(chatUser ->  chatClient.getChat(chatUser.getChatId()).getBody())
                .collect(Collectors.toList());
    }

    public List<UserEntityDto> getAllUsersByChat(Long chatId) {
        var chat = chatClient.getChat(chatId);
        return chatUserRepository.getChatUserEntitiesByChatId(chat.getBody().getId())
                .stream()
                .map(chatUser ->  userClient.getUserById(chatUser.getUserId()).getBody())
                .collect(Collectors.toList());
    }


//    @Transactional
//    public ChatEntityDto createChat(String firstLogin, String secondLogin, String message) {
//        var firstUser = userClient.getUser(firstLogin);
//        var secondUser = userClient.getUser(secondLogin);
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
