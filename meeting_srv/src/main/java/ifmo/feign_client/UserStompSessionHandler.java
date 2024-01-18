package ifmo.feign_client;

import ifmo.dto.UserEntityDto;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import reactor.util.annotation.NonNull;

import java.lang.reflect.Type;

@Getter
public class UserStompSessionHandler extends StompSessionHandlerAdapter {

    private final Logger logger = LogManager.getLogger(UserStompSessionHandler.class);

    private UserEntityDto result;

    @Override
    public void afterConnected(StompSession session, @NonNull StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
    }

    @Override
    public void handleException(@NonNull StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(@NonNull StompHeaders headers) {
        return UserEntityDto.class;
    }

    @Override
    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
        result = (UserEntityDto) payload;
        logger.info("Received : " + result.toString() + " from UserService");
    }

}