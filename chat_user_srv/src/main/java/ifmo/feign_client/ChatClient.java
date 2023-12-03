package ifmo.feign_client;

import ifmo.dto.ChatEntityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "chat-srv-eureka-client", path = "/api/v1/chats")
public interface ChatClient {
    @GetMapping("/entity/{chat_id}")
    public ResponseEntity<ChatEntityDto> getChat(@PathVariable(value = "chat_id") long chatId);

}
