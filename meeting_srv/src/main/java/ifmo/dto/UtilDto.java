package ifmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UtilDto implements Serializable {
    private Long eventId;
    private String userLogin;
    private String token;
}
