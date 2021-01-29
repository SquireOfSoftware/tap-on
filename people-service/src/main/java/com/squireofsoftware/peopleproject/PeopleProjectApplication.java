package com.squireofsoftware.peopleproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@EnableAutoConfiguration
@EntityScan(basePackages =  {"com.squireofsoftware.peopleproject.entities"})
@ConfigurationPropertiesScan(basePackages = {"com.squireofsoftware.peopleproject"})
public class PeopleProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(PeopleProjectApplication.class, args);
	}

}
