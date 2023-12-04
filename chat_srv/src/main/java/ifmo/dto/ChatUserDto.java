package ifmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatUserDto {
    Long chatId;
    Long userId;
}
