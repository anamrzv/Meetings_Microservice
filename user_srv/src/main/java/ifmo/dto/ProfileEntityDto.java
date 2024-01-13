package ifmo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ifmo.model.ProfileEntity;
import ifmo.validator.Birthday;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class ProfileEntityDto implements Serializable {

    Long id;
    @Pattern(message = "Неправильный формат имени: разрешены только буквы русского алфавита, от 1 до 30 символов", regexp = "[\u0410-\u042F\u0430-\u044F\u0451\u0401]{1,30}")
    @NotBlank(message = "Имя пользователя обязательно")
    String firstName;
    @Pattern(message = "Неправильный формат фамилии: разрешены только буквы русского алфавита, от 1 до 30 символов", regexp = "[\u0410-\u042F\u0430-\u044F\u0451\u0401]{1,30}")
    @NotBlank(message = "Фамилия пользователя обязательна")
    String lastName;
    @NotNull(message = "Дата рождения пользователя обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Birthday(message = "Пользователю должно быть от 16 лет")
    LocalDate dateOfBirth;
    @Pattern(message = "Неправильный формат номера телефона", regexp = "^\\+7[0-9]{10}$")
    @NotBlank(message = "Номер телефона пользователя отсутствует")
    String phone;
    @Email(message = "Неправильный формат электронной почты")
    @NotBlank(message = "Почта пользователя отсутствует")
    String mail;
    @NotBlank(message = "Фото профиля пользователя отсутствует")
    String icon;
    @JsonIgnore
    String secondUser = "";

    public ProfileEntityDto(ProfileEntity profileEntity) {
        this.id = profileEntity.getId();
        this.firstName = profileEntity.getFirstName();
        this.lastName = profileEntity.getLastName();
        this.dateOfBirth = profileEntity.getDateOfBirth();
        this.phone = profileEntity.getPhone();
        this.mail = profileEntity.getMail();
        this.icon = profileEntity.getIcon();
    }
}

