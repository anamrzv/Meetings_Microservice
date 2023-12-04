package ifmo.csr;

import ifmo.model.ChatUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUserEntity, Long> {
    Optional<ChatUserEntity> getChatUserEntitiesByUserId(Long id);

    Optional<ChatUserEntity> getChatUserEntitiesByChatId(Long id);

}
