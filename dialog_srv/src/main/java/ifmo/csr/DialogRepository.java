package ifmo.csr;

import ifmo.model.DialogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<DialogEntity, Long> {
    Optional<DialogEntity> getChatUserEntitiesByUserId(Long id);

    Optional<DialogEntity> getChatUserEntitiesByChatId(Long id);

}
