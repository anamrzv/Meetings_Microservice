package ifmo.repository;

import ifmo.model.ChatEntity;
import ifmo.model.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    Page<MessageEntity> findAllByChat(ChatEntity chat, Pageable pageable);

}
