package br.com.arquitetura;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@OpenAPIDefinition(
		info = @Info(
				title = "API de Logística e Rastreamento de Encomendas (Event Sourcing + CQRS)",
				version = "1.0.0",
				description = "Arquitetura orientada a eventos utilizando Java 21, Spring Boot, EventStoreDB e PostgreSQL."
		)
)
public class EventSourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventSourceApplication.class, args);
	}

}
