package org.squire.checkin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan
public class CheckInService {
    public static void main(String[] args) {
        SpringApplication.run(CheckInService.class, args);
    }
}
