package ifmo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ifmo.model.EventEntity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class EventEntityDto implements Serializable {
    Long id;
    @NotBlank(message = "Название мероприятия обязательно")
    @Length(message = "Название мероприятия должно быть от 1 до 40 символов", min = 1, max = 40)
    String name;
    @Min(message = "Возрастное ограницение не может быть меньше 0", value = 0)
    @Max(message = "Возрастное ограничение не может быть больше 21", value = 21)
    int ageLimit;
    @PositiveOrZero
    int maxOfVisitors;
    @NotBlank(message = "Описание мероприятия обязательно")
    String description;
    @NotBlank(message = "Изображение мероприятия обязательно")
    String image;
    @NotNull(message = "Дата проведения мероприятия обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime startDate;

    public EventEntityDto(EventEntity eventEntity) {
        id = eventEntity.getId();
        name = eventEntity.getName();
        ageLimit = eventEntity.getAgeLimit();
        maxOfVisitors = eventEntity.getMaxOfVisitors();
        description = eventEntity.getDescription();
        image = eventEntity.getImage();
        startDate = eventEntity.getStartDate();
    }
}