package ifmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableWebSecurity
public class DialogSrvApplication {

    public static void main(String[] args) {
        SpringApplication.run(DialogSrvApplication.class, args);
    }

}
