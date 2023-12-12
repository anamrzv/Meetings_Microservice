package ifmo.dto;

import ifmo.model.ChatEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Getter
@AllArgsConstructor
public class ChatEntityDto implements Serializable {
    Long id;
    LocalDateTime creationDate;

    public ChatEntityDto(ChatEntity chatEntity) {
        id = chatEntity.getId();
        creationDate = chatEntity.getCreationDate();
    }
}