package ifmo.csr;

import ifmo.dto.UserEntityDto;
import ifmo.exceptions.CustomInternalException;
import ifmo.feign_client.UserClient;
import ifmo.model.MeetingEntity;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserClient userClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public void addEventToInteresting(long eventId, String userLogin) {
        MeetingEntity meetingEntity = new MeetingEntity();

        CircuitBreaker breaker = circuitBreakerFactory.create("circuitbreaker");
        var userResponse = breaker.run(() -> userClient.getUser(userLogin), throwable -> userClient.getUserFallback());
        if (userResponse.getStatusCode().is5xxServerError()) throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");

        var userId = Objects.requireNonNull(userResponse.getBody()).id();
        meetingEntity.setEventId(eventId);
        meetingEntity.setUserId(userId);
        meetingRepository.save(meetingEntity);
    }

    public List<UserEntityDto> getAllUsersByEvent(long eventId) {
        CircuitBreaker breaker = circuitBreakerFactory.create("circuitbreaker");

        var meetingsForEvent = meetingRepository.findMeetingEntitiesByEventId(eventId);
        return meetingsForEvent.stream()
                .map(MeetingEntity::getUserId)
                .map(id -> breaker.run(() -> userClient.getUserById(id), throwable -> userClient.getUserFallback()))
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(HttpEntity::getBody)
                .collect(Collectors.toList());
    }
}
