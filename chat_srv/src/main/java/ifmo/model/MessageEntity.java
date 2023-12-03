package ifmo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity(name = "Message")
@Table(name = "message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_id_seq")
    @SequenceGenerator(name = "message_id_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "У сообщения должно быть содержание")
    private String content;

    @Column(name = "time_to_send", insertable=false)
    private LocalDateTime timeToSend;

    private Long sender;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;
}
