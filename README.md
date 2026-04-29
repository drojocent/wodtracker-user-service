# WODTracker User Service

Microservicio REST que gestiona autenticación, perfiles de usuario y administración de cuentas. Implementa JWT OAuth2 con Spring Security.

## Características

- Autenticación con JWT
- Registro de nuevos usuarios
- Gestión de perfiles
- Cambio de contraseña
- Notificaciones por email
- Panel administrativo de usuarios
- Validación de entrada
- Role-based access control


## Instalación

### Desarrollo local con H2

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

Accesible en http://localhost:8080

### Docker

```bash
cd ../docker/
docker compose up user-service
```

## Scripts

```bash
mvn clean install          # Build completo
mvn clean package          # Build sin tests
mvn test                   # Ejecutar tests
mvn test -Dtest=NombreTest # Test específico
mvn spring-boot:run        # Ejecutar aplicación
```

## Estructura

```
src/main/java/com/wodtracker/userservice/
├── config/          # Configuración Spring
├── controller/      # REST Endpoints
├── service/         # Lógica de negocio
├── repository/      # Acceso a datos (JPA)
├── entity/          # Entidades JPA
├── dto/             # Data Transfer Objects
├── mapper/          # Entity-DTO mapping
├── security/        # Autenticación/Autorizacion
├── exception/       # Excepciones personalizadas
└── Application.java # Main
```

## Endpoints API

Autenticación (público):
```
POST /auth/login
Content-Type: application/json
{"email": "user@example.com", "password": "pass"}
```

Registro (público):
```
POST /users
Content-Type: application/json
{"email": "new@example.com", "password": "pass", "firstName": "John", "lastName": "Doe"}
```

Perfil (autenticado):
```
GET /users/me
Authorization: Bearer <token>

PUT /users/me
Authorization: Bearer <token>
```

Admin:
```
GET /admin/users
GET /admin/users/{id}
DELETE /admin/users/{id}
PUT /admin/users/{id}/role
```

## Variables de entorno

```env
# Base de datos
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=userdb
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=password

# JWT
JWT_SECRET=your-256-bit-secret-key
JWT_EXPIRATION_MINUTES=60

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-password
MAIL_FROM=noreply@wodtracker.com
```

## Testing

```bash
mvn test
mvn test jacoco:report
```

Tests en `src/test/java/com/wodtracker/userservice/`

## Documentación API

Swagger UI disponible en:
```
http://localhost:8080/swagger-ui.html
JSON: http://localhost:8080/v3/api-docs
```

## Seguridad

- JWT con firma HS256
- BCrypt password hashing
- CORS configurado
- Stateless authentication
- Role-based access control
