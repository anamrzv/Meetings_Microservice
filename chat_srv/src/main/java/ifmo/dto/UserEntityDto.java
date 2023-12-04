package ifmo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.Objects;

@Value
@AllArgsConstructor
public class UserEntityDto implements Serializable {
    @NotNull
    Long id;
    @Pattern(message = "Неправильный формат логина: длина логина от 3 до 20 символов, разрешены только цифры, буквы латинского и русского алфавита, а также знаки _ и -", regexp = "[a-zA-Z\u0430-\u044F\u0410-\u042F\u0451\u04010-9_.]{3,20}")
    @NotBlank(message = "Логин обязателен")
    String login;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntityDto that = (UserEntityDto) o;
        return login.equals(that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}