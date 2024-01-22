package ifmo.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class DialogEntityDto implements Serializable {
    Long chatId;
    Long userId;
}
