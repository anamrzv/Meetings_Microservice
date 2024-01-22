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

    private static final String addQueue = "add-queue";
    private static final String getQueue = "get-queue";

    @Bean
    public Queue addQueue() {
        return new Queue(addQueue, false);
    }

    @Bean
    public Queue getQueue() {
        return new Queue(getQueue, false);
    }


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public Binding addBinding(Queue addQueue, DirectExchange exchange) {
        return BindingBuilder.bind(addQueue).to(exchange).with("add");
    }

    @Bean
    Binding getBinding(Queue getQueue, DirectExchange exchange) {
        return BindingBuilder.bind(getQueue).to(exchange).with("get");
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("ifmo.dto.*", "java.*", "jakarta.servlet.http.*", "org.springframework.amqp.*", "ifmo.exceptions.*"));
        return converter;
    }
}
