package ifmo.csr;

import ifmo.model.DialogEntity;
import ifmo.dto.ChatEntityDto;
import ifmo.dto.UserEntityDto;
import ifmo.feign_client.ChatClient;
import ifmo.feign_client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogService {

    private final DialogRepository dialogRepository;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private UserClient userClient;

    public List<ChatEntityDto> getAllChatsByUserLogin(String userLogin) {
        var user = userClient.getUser(userLogin);
        return dialogRepository.getChatUserEntitiesByUserId(Objects.requireNonNull(user.getBody()).id())
                .stream()
                .map(chatUser -> chatClient.getChat(chatUser.getChatId()).getBody())
                .collect(Collectors.toList());
    }

    public List<UserEntityDto> getAllUsersByChat(Long chatId) {
        var chat = chatClient.getChat(chatId);
        return dialogRepository.getChatUserEntitiesByChatId(Objects.requireNonNull(chat.getBody()).id())
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
