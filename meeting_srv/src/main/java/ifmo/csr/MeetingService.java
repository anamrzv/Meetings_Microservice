package ifmo.csr;

import ifmo.dto.UserEntityDto;
import ifmo.dto.UtilDto;
import ifmo.exceptions.CustomExistsException;
import ifmo.feign_client.UserWebSocketClient;
import ifmo.model.MeetingEntity;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
@EnableRabbit
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserWebSocketClient userWebSocketClient;

    private static final String addQueue = "add-queue";
    private static final String getQueue = "get-queue";

    @RabbitListener(queues = addQueue, returnExceptions = "true")
    public boolean addEventToInteresting(UtilDto utilDto) {
        MeetingEntity meetingEntity = new MeetingEntity();

        userWebSocketClient.getUserByLoginRequest(utilDto.getUserLogin());
        var userId = userWebSocketClient.getUserByLoginResponse().getId();

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
        var meetingsForEvent = meetingRepository.findMeetingEntitiesByEventId(utilDto.getEventId());
        List<UserEntityDto> allUsers = new LinkedList<>();
        for (MeetingEntity m : meetingsForEvent) {
            userWebSocketClient.getUserByIdRequest(m.getUserId());
            allUsers.add(userWebSocketClient.getUserByIdResponse());
        }
        return allUsers;
    }
}
