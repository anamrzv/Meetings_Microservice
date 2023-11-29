package ifmo.dto;

import ifmo.model.ChatEntity;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Getter
public class ChatEntityDto implements Serializable {
    Long id;
    LocalDateTime creationDate;
    //Set<UserEntityDto> users;

    public ChatEntityDto(ChatEntity chatEntity) {
        id = chatEntity.getId();
        creationDate = chatEntity.getCreationDate();
        //users = chatEntity.getUsers().stream().map(UserEntityDto::new).collect(Collectors.toSet());
    }
}