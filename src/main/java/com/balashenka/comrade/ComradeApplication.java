package com.balashenka.comrade;

import com.balashenka.comrade.configuration.ComradeProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(value = {ComradeProperty.class})
@SpringBootApplication
public class ComradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComradeApplication.class, args);
    }
}
