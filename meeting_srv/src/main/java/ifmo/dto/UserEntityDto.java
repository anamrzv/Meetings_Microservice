package ifmo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntityDto implements Serializable {
    Long id;
    @Pattern(message = "Неправильный формат логина: длина логина от 3 до 20 символов, разрешены только цифры, буквы латинского и русского алфавита, а также знаки _ и -", regexp = "[a-zA-Z\u0430-\u044F\u0410-\u042F\u0451\u04010-9_.]{3,20}")
    @NotBlank(message = "Логин обязателен")
    String login;
    Long profile;

}