package ifmo.dto;

import lombok.Getter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Getter
public class ChatEntityDto implements Serializable {
    Long id;
    LocalDateTime creationDate;
}