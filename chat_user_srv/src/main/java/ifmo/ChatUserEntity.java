package ifmo;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "user_chat")
@Getter
public class ChatUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "user_id")
    private Long userId;
}
