package ifmo.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CreateChatDto implements Serializable {
    private String userLogin;
    private String secondUserLogin;
    private String message;
    private String header;
    private Long chatId;
}
