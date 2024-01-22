package ifmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class DialogEntityDto implements Serializable {
    Long chatId;
    Long userId;
}
