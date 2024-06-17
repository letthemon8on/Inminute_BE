package org.example.inminute_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InminuteDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(InminuteDemoApplication.class, args);
	}

}
