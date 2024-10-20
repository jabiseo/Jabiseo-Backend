package com.jabiseo.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(
		scanBasePackages = {
				"com.jabiseo.infra.kafka",
				"com.jabiseo.notification"
		}
)
public class JabiseoNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(JabiseoNotificationApplication.class, args);
	}

}
