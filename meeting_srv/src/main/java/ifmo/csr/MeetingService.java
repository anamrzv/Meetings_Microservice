package ifmo.csr;

import ifmo.dto.UserEntityDto;
import ifmo.feign_client.UserClient;
import ifmo.model.MeetingEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserClient userClient;

    public void addEventToInteresting(long eventId, String userLogin) {
        MeetingEntity meetingEntity = new MeetingEntity();
        var userId = Objects.requireNonNull(userClient.getUser(userLogin).getBody()).id();
        meetingEntity.setEventId(eventId);
        meetingEntity.setUserId(userId);
        meetingRepository.save(meetingEntity);
    }

    public List<UserEntityDto> getAllUsersByEvent(long eventId) {
        var meetingsForEvent = meetingRepository.findMeetingEntitiesByEventId(eventId);
        return meetingsForEvent.stream()
                .map(MeetingEntity::getUserId)
                .map(id -> userClient.getUserById(id).getBody())
                .collect(Collectors.toList());
    }
}
