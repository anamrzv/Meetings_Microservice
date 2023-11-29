package ifmo.repository;

import ifmo.model.CharacteristicEntity;
import ifmo.model.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    List<EventEntity> getEventEntitiesByAgeLimitBefore(int ageLimit);

    List<EventEntity> getEventEntitiesByCharacteristicsIn(Set<CharacteristicEntity> characteristicEntities);
}
