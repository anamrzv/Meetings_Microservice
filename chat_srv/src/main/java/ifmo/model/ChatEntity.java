package ifmo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity(name = "Chat")
@Table(name = "chat")
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_gen")
    @SequenceGenerator(name = "chat_gen", sequenceName = "chat_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "creation_date", insertable = false)
    private LocalDateTime creationDate;

//    @ManyToMany(mappedBy = "chats", fetch = FetchType.EAGER)
//    private Set<UserEntity> users = new HashSet<>();

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ChatEntity that = (ChatEntity) o;
//        var usersArr = users.toArray();
//        var usersArrO = that.users.toArray();
//        return (usersArr[0].equals(usersArrO[0]) && usersArr[1].equals(usersArrO[1]));
//    }

    @Override
    public String toString() {
        return "ChatEntity{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate);
    }
}
