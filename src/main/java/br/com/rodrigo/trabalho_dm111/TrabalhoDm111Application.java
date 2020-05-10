package br.com.rodrigo.trabalho_dm111;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@SpringBootApplication
public class TrabalhoDm111Application {

	public static void main(String[] args) {
		SpringApplication.run(TrabalhoDm111Application.class, args);
	}

}
