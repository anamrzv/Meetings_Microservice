package ifmo.repository;

import ifmo.model.ChatEntity;
import ifmo.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findAllByChat(ChatEntity chat);

}
