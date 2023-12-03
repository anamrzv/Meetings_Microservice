package ifmo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Value
@AllArgsConstructor
public record UserEntityDto(Long id,
                            @Pattern(message = "Неправильный формат логина: длина логина от 3 до 20 символов, разрешены только цифры, буквы латинского и русского алфавита, а также знаки _ и -", regexp = "[a-zA-Zа-яА-ЯёЁ0-9_.]{3,20}")
                            @NotBlank(message = "Логин обязателен")
                            String login) implements Serializable {
}