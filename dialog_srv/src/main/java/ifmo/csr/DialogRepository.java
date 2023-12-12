package ifmo.csr;

import ifmo.model.DialogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DialogRepository extends JpaRepository<DialogEntity, Long> {
    List<DialogEntity> getChatUserEntitiesByUserId(Long id);

    List<DialogEntity> getChatUserEntitiesByChatId(Long id);

}
