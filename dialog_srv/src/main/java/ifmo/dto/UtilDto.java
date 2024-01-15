package ifmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class UtilDto implements Serializable {
    private String userLogin;
    private String token;
    private Long chatId;
    private Long userId;
}
