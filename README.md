# Academia — Plataforma de Microservicios (DSY1103)

Sistema de gestión académica construido como una arquitectura de **microservicios** con
**Spring Boot 4**, un **API Gateway** (Spring Cloud Gateway) como única puerta de entrada,
**MySQL** como base de datos compartida y **JWT** para autenticación. Todo se levanta con
**Docker Compose**.

> Evaluación Parcial 3 · Asignatura DSY1103 · Docente: Carlos Abarzúa Castro

## 👥 Integrantes

<!-- Completar con los nombres reales del equipo -->
- Nombre Apellido — RUT / sección
- Nombre Apellido — RUT / sección

## 🏗️ Arquitectura

- **Patrón por capas (CSR):** cada servicio separa `controller` (orquesta peticiones),
  `service` (lógica de negocio), `repository` (acceso a datos) y `model`/`dto`.
- **API Gateway (puerto 8080):** única entrada pública. Valida el JWT (`JwtAuthFilter`),
  rutea por prefijo de ruta y agrega la documentación Swagger de todos los servicios.
- **Base de datos compartida:** MySQL `academia_noafer`. Credenciales de login en
  `auth_usuarios` (auth-service) y perfiles en `usuarios` (user-service), unidos por `run`.
- **Comunicación entre servicios:** vía `WebClient` (REST), p. ej. pago-service valida la
  existencia del curso y user-service provisiona la credencial en auth-service.

## 📦 Microservicios

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| **api-gateway** | 8080 | Entrada única: autenticación JWT, ruteo y Swagger agregado |
| pago-service | 8082 | Pagos y transacciones |
| clases-service | 8083 | Cursos, clases y categorías |
| evaluaciones-service | 8084 | Evaluaciones, preguntas y alternativas |
| inscripciones-service | 8085 | Inscripciones y cupos |
| user-service | 8086 | Usuarios (perfil) y roles |
| auth-service | 8087 | Login, registro y emisión de JWT |
| certificado-service | 8088 | Certificados |
| calificaciones-service | 8089 | Calificaciones y promedios |
| mysql | 3307→3306 | Base de datos compartida `academia_noafer` |

## 🔀 Rutas del Gateway (todo vía `http://localhost:8080`)

| Prefijo | Servicio destino |
|---|---|
| `/api/auth/**` | auth-service *(público)* |
| `/api/usuarios/**`, `/api/roles/**` | user-service |
| `/api/pagos/**`, `/api/transacciones/**` | pago-service |
| `/api/cursos/**`, `/api/categorias/**`, `/api/clases/**` | clases-service |
| `/api/evaluaciones/**`, `/api/preguntas/**`, `/api/alternativas/**` | evaluaciones-service |
| `/api/inscripciones/**`, `/api/cupos/**` | inscripciones-service |
| `/api/calificaciones/**`, `/api/promedios/**` | calificaciones-service |
| `/api/certificados/**` | certificado-service |
| `/v3/api-docs/{servicio}` | documentación OpenAPI de cada servicio |

## 📖 Swagger / OpenAPI

- **Swagger UI (agregado):** http://localhost:8080/swagger-ui.html
  → desplegable arriba a la derecha para elegir el servicio (1. Auth … 8. Inscripciones).
- Cada servicio expone su propio `/v3/api-docs`; el gateway lo reescribe a
  `/v3/api-docs/{servicio}`.

## 🔐 Autenticación

1. **Login:** `POST /api/auth/login` con `{ "run": "...", "clave": "..." }` → devuelve un **JWT** (HS256, 1 h).
2. **Rutas protegidas:** enviar `Authorization: Bearer <token>`. El gateway valida el token y lo reenvía.
3. **Públicas:** `/api/auth/**`, `/swagger-ui.html`, `/v3/api-docs/**`.

**Usuarios de prueba (seed):** la clave de todos es `clave`. Ej.: `run = 10492048-9`, `clave = clave`.

## 🚀 Cómo ejecutar (local con Docker)

**Requisitos:** Docker Desktop en ejecución + conexión a internet (solo para la primera build).

```bash
# Desde la raíz del repositorio
docker compose up --build -d      # construir e iniciar todo
docker compose ps                 # ver estado
docker compose logs -f auth-service   # ver logs de un servicio
docker compose down               # detener todo
```

Luego abrir **http://localhost:8080/swagger-ui.html**.

> Nota: cada `Dockerfile` compila el servicio dentro de Docker (build multi-stage), por lo que
> **no necesitas Java ni Maven instalados**. Para empezar con una base de datos limpia:
> `docker compose up -d --force-recreate -V`.

## ✅ Pruebas unitarias

Cada servicio tiene pruebas unitarias con **JUnit 5 + Mockito** (estructura *Given-When-Then*,
mocks de repositorios/`WebClient`, validación de reglas de negocio) bajo `src/test/java`.

```bash
# Ejecutar las pruebas de un servicio
cd pago-service
./mvnw test
```

## 🛠️ Stack técnico

- Java 21 · Spring Boot 4.0.x · Spring Cloud Gateway (WebFlux)
- Spring Data JPA · MySQL 8 · springdoc-openapi (Swagger) · JWT (jjwt, HS256)
- JUnit 5 · Mockito · Docker / Docker Compose

## 📁 Entrega

- **Repositorio GitHub:** https://github.com/fer-agm/academia
- Proyecto entregado en AVA.
