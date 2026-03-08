package com.gamerlink.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {
                "com.gamerlink.app",
                "com.gamerlink.identity",
                "com.gamerlink.profile",
                "com.gamerlink.media",
                "com.gamerlink.shared"
                // Add other modules as needed
        }
)
@EnableJpaRepositories(
        basePackages = {
                "com.gamerlink.identity.repository",
                "com.gamerlink.profile.repository",
                "com.gamerlink.media.repository"
                // Add other module repositories
        }
)
@EntityScan(
        basePackages = {
                "com.gamerlink.identity.model",
                "com.gamerlink.profile.model",
                "com.gamerlink.media.model"
                // Add other module entities
        }
)
@ConfigurationPropertiesScan(
        basePackages = "com.gamerlink"
)
@EnableScheduling
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
