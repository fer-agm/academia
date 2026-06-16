package com.example.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}


// {
//     "run": "12345678-9",
//     "clave": "1234"
// }

// te da el token, vas al get en postman y en headers pones Authorization: Bearer 'token' y te da acceso.