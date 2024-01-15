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

    private static final String findQueue = "find-queue";
    private static final String sendQueue = "send-queue";
    private static final String createQueue = "create-queue";
    private static final String idQueue = "id-queue";

    @Bean
    public Queue findQueue() {
        return new Queue(findQueue, false);
    }

    @Bean
    public Queue sendQueue() {
        return new Queue(sendQueue, false);
    }

    @Bean
    public Queue createQueue() {
        return new Queue(createQueue, false);
    }

    @Bean
    public Queue idQueue() {
        return new Queue(idQueue, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public Binding fingBinding(Queue findQueue, DirectExchange exchange) {
        return BindingBuilder.bind(findQueue).to(exchange).with("find");
    }

    @Bean
    Binding sendBinding(Queue sendQueue, DirectExchange exchange) {
        return BindingBuilder.bind(sendQueue).to(exchange).with("send");
    }

    @Bean
    Binding createBinding(Queue createQueue, DirectExchange exchange) {
        return BindingBuilder.bind(createQueue).to(exchange).with("create");
    }

    @Bean
    Binding idBinding(Queue idQueue, DirectExchange exchange) {
        return BindingBuilder.bind(idQueue).to(exchange).with("id");
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("ifmo.dto.*", "java.*", "jakarta.servlet.http.*", "org.springframework.amqp.*", "ifmo.exceptions.*"));
        return converter;
    }
}
