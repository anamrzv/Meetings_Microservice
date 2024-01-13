package ifmo.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitConfiguration {

    private static final String registerQueue = "register-queue";
    private static final String registerAdminQueue = "register-admin-queue";
    private static final String authQueue = "auth-queue";


    private static final String showProfileQueue = "show-profile-queue";
    private static final String updateProfileQueue = "update-profile-queue";


    private static final String showUserQueue = "show-user-queue";
    private static final String showUserIdQueue = "show-user-id-queue";


    @Bean
    public Queue registerQueue() {
        return new Queue(registerQueue, false);
    }

    @Bean
    public Queue registerAdminQueue() {
        return new Queue(registerAdminQueue, false);
    }

    @Bean
    public Queue authQueue() {
        return new Queue(authQueue, false);
    }

    @Bean
    public Queue updateProfileQueue() {
        return new Queue(updateProfileQueue, false);
    }

    @Bean
    public Queue showProfileQueue() {
        return new Queue(showProfileQueue, false);
    }

    @Bean
    public Queue showUserQueue() {
        return new Queue(showUserQueue, false);
    }

    @Bean
    public Queue showUserIdQueue() {
        return new Queue(showUserIdQueue, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("direct-exchange");
    }

    @Bean
    public Binding registerBinding(Queue registerQueue, DirectExchange exchange) {
        return BindingBuilder.bind(registerQueue).to(exchange).with("register");
    }

    @Bean
    Binding registerAdminBinding(Queue registerAdminQueue, DirectExchange exchange) {
        return BindingBuilder.bind(registerAdminQueue).to(exchange).with("register-admin");
    }

    @Bean
    Binding authBinding(Queue authQueue, DirectExchange exchange) {
        return BindingBuilder.bind(authQueue).to(exchange).with("auth");
    }

    @Bean
    Binding showProfileBinding(Queue showProfileQueue, DirectExchange exchange) {
        return BindingBuilder.bind(showProfileQueue).to(exchange).with("show-profile");
    }

    @Bean
    public Binding updateProfileBinding(Queue updateProfileQueue, DirectExchange exchange) {
        return BindingBuilder.bind(updateProfileQueue).to(exchange).with("update-profile");
    }

    @Bean
    Binding showUserBinding(Queue showUserQueue, DirectExchange exchange) {
        return BindingBuilder.bind(showUserQueue).to(exchange).with("show-user");
    }

    @Bean
    Binding showUserIdBinding(Queue showUserIdQueue, DirectExchange exchange) {
        return BindingBuilder.bind(showUserIdQueue).to(exchange).with("show-user-id");
    }


    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("ifmo.dto.*", "java.*"));
        return converter;
    }
}
