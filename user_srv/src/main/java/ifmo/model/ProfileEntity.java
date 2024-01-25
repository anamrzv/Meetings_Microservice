package ifmo.model;

import ifmo.validator.Birthday;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "profile")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sign_up_date", insertable = false)
    private LocalDateTime signUpDate;

    @Pattern(regexp = "[А-Яа-яёЁ]{1,30}", message = "Неправильный формат имени: разрешены только буквы русского алфавита, от 1 до 30 символов")
    @NotBlank(message = "Имя пользователя обязательно")
    @Column(name = "first_name")
    private String firstName;

    @Pattern(regexp = "[А-Яа-яёЁ]{1,30}", message = "Неправильный формат фамилии: разрешены только буквы русского алфавита, от 1 до 30 символов")
    @NotBlank(message = "Фамилия пользователя обязательна")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
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

    @Column(name = "icon", columnDefinition="bytea")
    private byte[] icon;
}
