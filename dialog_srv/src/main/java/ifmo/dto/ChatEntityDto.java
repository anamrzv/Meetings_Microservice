package ifmo.dto;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class ChatEntityDto implements Serializable {
    private Long id;
    private LocalDateTime creationDate;
}