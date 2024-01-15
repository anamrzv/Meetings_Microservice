package ifmo.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitConfiguration {

    private static final String chatQueue = "chat-queue";
    private static final String saveQueue = "save-queue";
    private static final String userQueue = "user-queue";

    @Bean
    public Queue chatQueue() {
        return new Queue(chatQueue, false);
    }
    @Bean
    public Queue saveQueue() {
        return new Queue(saveQueue, false);
    }
    @Bean
    public Queue userQueue() { return new Queue(userQueue, false); }
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, DirectExchange exchange) {
        return BindingBuilder.bind(chatQueue).to(exchange).with("chat");
    }

    @Bean
    Binding saveBinding(Queue saveQueue, DirectExchange exchange) {
        return BindingBuilder.bind(saveQueue).to(exchange).with("save");
    }

    @Bean
    Binding userBinding(Queue userQueue, DirectExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with("user");
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("ifmo.dto.*", "java.*", "org.springframework.data.domain.*", "jakarta.servlet.http.*", "org.springframework.amqp.*", "ifmo.exceptions.*", "*"));
        return converter;
    }
}
