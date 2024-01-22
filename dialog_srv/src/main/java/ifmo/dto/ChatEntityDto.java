package ifmo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatEntityDto implements Serializable {
    private Long id;
    private LocalDateTime creationDate;
}