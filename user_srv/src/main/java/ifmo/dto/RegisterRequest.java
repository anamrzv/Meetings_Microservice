package ifmo.dto;

import ifmo.validator.Birthday;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest implements Serializable {
    @Pattern(regexp = "[a-zA-Zа-яА-ЯёЁ0-9_.]{3,20}", message = "Неправильный формат логина: длина логина от 3 до 20 символов, разрешены только цифры, буквы латинского и русского алфавита, а также знаки _ и -")
    @NotBlank(message = "Логин обязателен")
    private String login;

    @NotBlank(message = "Пароль обязателен")
    @Length(min = 5, message = "Пароль должен быть минимум 5 символов")
    private String password;

    @Pattern(regexp = "[А-Яа-яёЁ]{1,30}", message = "Неправильный формат имени: разрешены только буквы русского алфавита, от 1 до 30 символов")
    @NotBlank(message = "Имя пользователя обязательно")
    private String firstName;

    @Pattern(regexp = "[А-Яа-яёЁ]{1,30}", message = "Неправильный формат фамилии: разрешены только буквы русского алфавита, от 1 до 30 символов")
    @NotBlank(message = "Фамилия пользователя обязательна")
    private String lastName;

    @NotNull(message = "Дата рождения пользователя обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    @Birthday(message = "Пользователю должно быть от 16 лет")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\+7[0-9]{10}$", message = "Неправильный формат номера телефона")
    @NotBlank(message = "Номер телефона пользователя отсутствует")
    private String phone;

    @Email(message = "Неправильный формат электронной почты")
    @NotBlank(message = "Почта пользователя отсутствует")
    private String mail;

    @NotBlank(message = "Фото профиля пользователя отсутствует")
    private String icon;
}
