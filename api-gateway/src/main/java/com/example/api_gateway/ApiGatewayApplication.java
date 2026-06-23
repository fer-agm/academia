package com.example.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
// http://localhost:PUERTO/swagger-ui/index.html

// # 8080 api 
// # 8082 pagos
// # 8083 clases 
// # 8084 evaluaciones
// # 8085 inscripciones
// # 8086 usuarios
// # 8087 auth
// # 8088 certificados
// # 8089 calificaciones

// {
//     "run": "12345678-9",
//     "clave": "1234"
// }

// te da el token, vas al get en postman y en headers pones Authorization: Bearer 'token' y te da acceso.