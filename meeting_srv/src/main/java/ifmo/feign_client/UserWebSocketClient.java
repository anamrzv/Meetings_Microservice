package ifmo.feign_client;

import ifmo.dto.UserEntityDto;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;

@Service
public class UserWebSocketClient {
    private final UserStompSessionHandler sessionHandler;
    private final static String URL = "ws://localhost:9001/websocket-user";
    private final static String subscribeLogin = "/topic/loginResult";
    private final static String subscribeId = "/topic/idResult";
    private final static String sendUserByLogin = "/app/getUserByLoginWebsocket";
    private final static String sendUserById = "/app/getUserByIdWebsocket";
    private final StompSession session;

    public UserWebSocketClient() throws ExecutionException, InterruptedException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        sessionHandler = new UserStompSessionHandler();
        var future = stompClient.connectAsync(URL, sessionHandler);
        session = future.get();
        session.subscribe(subscribeLogin, sessionHandler);
        session.subscribe(subscribeId, sessionHandler);
    }

    public void getUserByLoginRequest(String login) {
        session.send(sendUserByLogin, login);
    }

    public UserEntityDto getUserByLoginResponse() {
        return sessionHandler.getResult();
    }

    public void getUserByIdRequest(Long id) {
        session.send(sendUserById, id);
    }

    public UserEntityDto getUserByIdResponse() {
        return sessionHandler.getResult();
    }


}
