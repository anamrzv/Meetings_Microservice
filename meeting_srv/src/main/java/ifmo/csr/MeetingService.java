package ifmo.csr;

import ifmo.dto.UserEntityDto;
import ifmo.dto.UtilDto;
import ifmo.exceptions.CustomExistsException;
import ifmo.exceptions.CustomInternalException;
import ifmo.feign_client.UserClient;
import ifmo.model.MeetingEntity;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@EnableRabbit
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserClient userClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    private static final String addQueue = "add-queue";
    private static final String getQueue = "get-queue";


    @RabbitListener(queues = addQueue, returnExceptions = "true")
    public boolean addEventToInteresting(UtilDto utilDto) {
        MeetingEntity meetingEntity = new MeetingEntity();

        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var userResponse = breaker.run(() -> userClient.getUser(utilDto.getUserLogin(), utilDto.getToken()), throwable -> userClient.getUserFallback());
        if (userResponse.getStatusCode().is5xxServerError()) throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");

        var userId = Objects.requireNonNull(userResponse.getBody()).id();
        var existing = meetingRepository.findMeetingEntityByEventIdAndUserId(utilDto.getEventId(), userId);
        if (existing.isEmpty()) {
            meetingEntity.setEventId(utilDto.getEventId());
            meetingEntity.setUserId(userId);
            meetingRepository.save(meetingEntity);
            return true;
        } else throw new CustomExistsException("Мероприятие уже добавлено!");
    }

    @RabbitListener(queues = getQueue, returnExceptions = "true")
    public List<UserEntityDto> getAllUsersByEvent(UtilDto utilDto) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");

        var meetingsForEvent = meetingRepository.findMeetingEntitiesByEventId(utilDto.getEventId());
        return meetingsForEvent.stream()
                .map(MeetingEntity::getUserId)
                .map(id -> breaker.run(() -> userClient.getUserById(id, utilDto.getToken()), throwable -> userClient.getUserFallback()))
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(HttpEntity::getBody)
                .collect(Collectors.toList());
    }
}
