package com.gamerlink.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = {
                "com.gamerlink.app",
                "com.gamerlink.identity",
                "com.gamerlink.shared"
                // Add other modules as needed
        }
)
@EnableJpaRepositories(
        basePackages = {
                "com.gamerlink.identity.repository"
                // Add other module repositories
        }
)
@EntityScan(
        basePackages = {
                "com.gamerlink.identity.model"
                // Add other module entities
        }
)
@ConfigurationPropertiesScan(
        basePackages = "com.gamerlink"
)
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
