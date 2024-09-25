package com.example.ToDo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class ToDoApplication {
	public static void main(String[] args) {
		SpringApplication.run(ToDoApplication.class, args);
	}

}
