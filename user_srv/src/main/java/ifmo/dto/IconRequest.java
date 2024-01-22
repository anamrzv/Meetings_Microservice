package ifmo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class IconRequest implements Serializable {
    private String login;
    private byte[] file;
}
