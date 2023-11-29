package ifmo.repository;

import ifmo.model.CharacteristicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacteristicRepository extends JpaRepository<CharacteristicEntity, Long> {
    Optional<CharacteristicEntity> getCharacteristicEntityByName(String name);
}
