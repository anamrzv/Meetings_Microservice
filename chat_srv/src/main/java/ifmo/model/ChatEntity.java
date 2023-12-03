package ifmo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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
}
