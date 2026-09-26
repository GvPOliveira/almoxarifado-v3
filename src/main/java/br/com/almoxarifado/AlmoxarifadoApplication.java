package br.com.almoxarifado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlmoxarifadoApplication {

	public static void main(String[] args) {

		System.out.println("DB_USERNAME = " + System.getenv("DB_USERNAME"));
		SpringApplication.run(AlmoxarifadoApplication.class, args);
	}

}
