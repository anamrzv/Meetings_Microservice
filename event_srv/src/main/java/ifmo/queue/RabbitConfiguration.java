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

    private static final String findAllQueue = "find-all-queue";
    private static final String findAllChildQueue = "find-child-queue";
    private static final String filterQueue = "filter-queue";
    private static final String idQueue = "id-queue";
    private static final String addQueue = "add-queue";
    private static final String updaterQueue = "update-queue";
    private static final String setQueue = "set-queue";
    private static final String removeQueue = "remove-queue";

    @Bean
    public Queue findAllQueue() {
        return new Queue(findAllQueue, false);
    }
    @Bean
    public Queue findAllChildQueue() {
        return new Queue(findAllChildQueue, false);
    }
    @Bean
    public Queue filterQueue() { return new Queue(filterQueue, false); }
    @Bean
    public Queue idQueue() {
        return new Queue(idQueue, false);
    }
    @Bean
    public Queue addQueue() {
        return new Queue(addQueue, false);
    }
    @Bean
    public Queue updateQueue() {
        return new Queue(updaterQueue, false);
    }
    @Bean
    public Queue setQueue() {
        return new Queue(setQueue, false);
    }
    @Bean
    public Queue removeQueue() {
        return new Queue(removeQueue, false);
    }
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public Binding findAllBinding(Queue findAllQueue, DirectExchange exchange) {
        return BindingBuilder.bind(findAllQueue).to(exchange).with("find-all");
    }

    @Bean
    Binding findAllChildBinding(Queue findAllChildQueue, DirectExchange exchange) {
        return BindingBuilder.bind(findAllChildQueue).to(exchange).with("find-child");
    }

    @Bean
    Binding filterBinding(Queue filterQueue, DirectExchange exchange) {
        return BindingBuilder.bind(filterQueue).to(exchange).with("filter");
    }

    @Bean
    Binding idBinding(Queue idQueue, DirectExchange exchange) {
        return BindingBuilder.bind(idQueue).to(exchange).with("id");
    }

    @Bean
    Binding addBinding(Queue addQueue, DirectExchange exchange) {
        return BindingBuilder.bind(addQueue).to(exchange).with("add");
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, DirectExchange exchange) {
        return BindingBuilder.bind(updateQueue).to(exchange).with("update");
    }

    @Bean
    Binding setBinding(Queue setQueue, DirectExchange exchange) {
        return BindingBuilder.bind(setQueue).to(exchange).with("set");
    }

    @Bean
    Binding removeBinding(Queue removeQueue, DirectExchange exchange) {
        return BindingBuilder.bind(removeQueue).to(exchange).with("remove");
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("ifmo.dto.*", "java.*", "org.springframework.data.domain.*", "jakarta.servlet.http.*", "org.springframework.amqp.*", "ifmo.exceptions.*"));
        return converter;
    }
}
