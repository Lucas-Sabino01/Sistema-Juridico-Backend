package com.lucas.projeto.juridico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class JuridicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JuridicoApplication.class, args);
	}

}
