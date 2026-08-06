package com.company.issuebot;

import com.company.issuebot.config.DbAccessProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DbAccessProperties.class)
public class IssueBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(IssueBotApplication.class, args);
    }
}
