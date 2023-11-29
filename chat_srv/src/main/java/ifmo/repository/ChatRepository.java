package ifmo.repository;

import ifmo.model.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    Optional<ChatEntity> getChatEntityById(Long id);

}
