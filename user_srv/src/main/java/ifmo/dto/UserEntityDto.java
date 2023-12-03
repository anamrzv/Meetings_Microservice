package ifmo.dto;

import ifmo.model.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Value
@AllArgsConstructor
public class UserEntityDto implements Serializable {
    Long id;
    @Pattern(message = "Неправильный формат логина: длина логина от 3 до 20 символов, разрешены только цифры, буквы латинского и русского алфавита, а также знаки _ и -", regexp = "[a-zA-Z\u0430-\u044F\u0410-\u042F\u0451\u04010-9_.]{3,20}")
    @NotBlank(message = "Логин обязателен")
    String login;

    public UserEntityDto(UserEntity userEntity) {
        id = userEntity.getId();
        login = userEntity.getLogin();
    }
}