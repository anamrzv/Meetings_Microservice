package ifmo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ifmo.model.MessageEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@AllArgsConstructor
@Getter
@Setter
public class MessageDTO implements Serializable {
    Long id;
    @NotBlank(message = "У сообщения должно быть содержание")
    String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timeToSend;

    public MessageDTO(MessageEntity messageEntity) {
        id = messageEntity.getId();
        content = messageEntity.getContent();
        timeToSend = messageEntity.getTimeToSend();
    }
}