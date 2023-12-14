package ifmo.csr;

import ifmo.model.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<MeetingEntity, Long> {
    List<MeetingEntity> findMeetingEntitiesByEventId(Long id);

    Optional<MeetingEntity> findMeetingEntityByEventIdAndUserId(Long eventId, Long userId);
}
