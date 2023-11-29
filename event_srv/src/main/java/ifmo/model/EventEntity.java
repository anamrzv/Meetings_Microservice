package ifmo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity(name = "Event")
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(min = 1, max = 40, message = "Название мероприятия должно быть от 1 до 40 символов")
    @NotBlank(message = "Название мероприятия обязательно")
    @Column(name = "name")
    private String name;

    @Column(name = "age_limit")
    @Min(value = 0, message = "Возрастное ограницение не может быть меньше 0")
    @Max(value = 21, message = "Возрастное ограничение не может быть больше 21")
    private int ageLimit;

    @Column(name = "max_of_visitors")
    @PositiveOrZero
    private int maxOfVisitors;

    @NotBlank(message = "Описание мероприятия обязательно")
    @Column(name = "description")
    private String description;

    @NotBlank(message = "Изображение мероприятия обязательно")
    @Column(name = "image")
    private String image;

    @NotNull(message = "Дата проведения мероприятия обязательна")
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "characteristic_event",
            joinColumns = {
                    @JoinColumn(name = "event_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "characteristic_id")
            })
    private Set<CharacteristicEntity> characteristics = new HashSet<>();

    public EventEntity(String name, int ageLimit, String image, String description, int maxOfVisitors, LocalDateTime startDate) {
        this.name = name;
        this.ageLimit = ageLimit;
        this.image = image;
        this.maxOfVisitors = maxOfVisitors;
        this.description = description;
        this.startDate = startDate;
    }

    public void removeCharacteristic(CharacteristicEntity characteristicEntity) {
        this.characteristics.remove(characteristicEntity);
        characteristicEntity.getEvents().remove(this);
    }

    public void addCharacteristic(CharacteristicEntity characteristicEntity) {
        try {
            this.characteristics.add(characteristicEntity);
            characteristicEntity.getEvents().add(this);
        } catch (IllegalArgumentException ignored) {

        }
    }
    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ageLimit=" + ageLimit +
                ", maxOfVisitors=" + maxOfVisitors +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return ageLimit == that.ageLimit && maxOfVisitors == that.maxOfVisitors && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(image, that.image) && Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, ageLimit, maxOfVisitors, description, image, startDate);
    }
}
