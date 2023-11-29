package ifmo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Characteristic")
@Table(name = "characteristic")
public class CharacteristicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "У характеристики должно быть название")
    @Length(min = 1, max = 10, message = "Длина характеристики должна быть от 0 до 10")
    private String name;

    @ManyToMany(mappedBy = "characteristics", fetch = FetchType.EAGER)
    private Set<EventEntity> events = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacteristicEntity that = (CharacteristicEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "CharacteristicEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

