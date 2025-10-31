# Task Manager - Sistema de Gestión de Tareas

## Descripción
Sistema CRUD para gestión de tareas personales desarrollado con Spring Boot, Spring Security, JWT y MySQL. Implementa autenticación, autorización basada en roles y documentación API con Swagger.

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL 8.0
- JWT (JSON Web Tokens)
- Swagger/OpenAPI 3.0
- Maven
- Lombok

## Características Principales
- ✅ CRUD completo de tareas
- ✅ Autenticación con JWT
- ✅ Autorización basada en roles (USER, ADMIN)
- ✅ Validaciones con Bean Validation
- ✅ Manejo de excepciones centralizado
- ✅ Documentación automática con Swagger
- ✅ Base de datos MySQL
- ✅ Programación funcional con Streams
- ✅ Uso de colecciones (Set, List)

## Roles y Permisos

### Usuario (USER)
- ✅ Crear tareas
- ✅ Ver sus propias tareas
- ❌ No puede actualizar tareas
- ❌ No puede eliminar tareas

### Administrador (ADMIN)
- ✅ Crear tareas
- ✅ Ver todas las tareas
- ✅ Actualizar cualquier tarea
- ✅ Eliminar cualquier tarea

## Requisitos Previos
- JDK 17 o superior
- Maven 3.6+
- MySQL 8.0+
- Postman (opcional, para pruebas)

## Configuración de Base de Datos

### 1. Crear la base de datos en MySQL
```sql
CREATE DATABASE taskmanager_db;
```

### 2. Configurar credenciales
Editar el archivo `src/main/resources/application.properties` con tus credenciales:
```properties
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

## Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/task-manager.git
cd task-manager
```

### 2. Compilar el proyecto
```bash
mvn clean install
```

### 3. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## Usuarios de Prueba

La aplicación crea automáticamente dos usuarios:

| Usuario | Contraseña | Rol | Email |
|---------|------------|-----|-------|
| admin | admin123 | ADMIN | admin@taskmanager.com |
| user | user123 | USER | user@taskmanager.com |

## Endpoints de la API

### Autenticación

#### Registrar Usuario
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "nuevo_usuario",
  "email": "nuevo@email.com",
  "password": "password123",
  "roles": ["user"]
}
```

#### Iniciar Sesión
```http
POST /api/auth/signin
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@taskmanager.com",
  "roles": ["ROLE_ADMIN"]
}
```

### Tareas (Requieren autenticación)

#### Crear Tarea
```http
POST /api/tasks
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Completar proyecto Spring Boot",
  "description": "Implementar todas las funcionalidades CRUD",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59"
}
```

#### Listar Todas las Tareas
```http
GET /api/tasks
Authorization: Bearer {token}
```

#### Obtener Tarea por ID
```http
GET /api/tasks/{id}
Authorization: Bearer {token}
```

#### Actualizar Tarea (Solo ADMIN)
```http
PUT /api/tasks/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Título actualizado",
  "description": "Descripción actualizada",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2024-12-31T23:59:59"
}
```

#### Eliminar Tarea (Solo ADMIN)
```http
DELETE /api/tasks/{id}
Authorization: Bearer {token}
```

## Estados de Tareas
- `PENDING` - Pendiente
- `IN_PROGRESS` - En progreso
- `COMPLETED` - Completada
- `CANCELLED` - Cancelada

## Prioridades
- `LOW` - Baja
- `MEDIUM` - Media
- `HIGH` - Alta
- `URGENT` - Urgente

## Documentación Swagger

Una vez iniciada la aplicación, acceder a:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs JSON: http://localhost:8080/api-docs

### Uso de Swagger
1. Ir a http://localhost:8080/swagger-ui.html
2. Hacer clic en "Authorize" (candado verde)
3. Ingresar el token en formato: `Bearer {tu-token-jwt}`
4. Probar los endpoints directamente desde la interfaz

## Estructura del Proyecto

```
task-manager/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── config/           # Configuraciones (Security, Swagger, DataInitializer)
│   │   │   ├── controller/       # Controladores REST
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── exception/        # Manejo de excepciones
│   │   │   ├── model/            # Entidades JPA
│   │   │   ├── repository/       # Repositorios Spring Data
│   │   │   ├── security/         # JWT y Security
│   │   │   └── service/          # Lógica de negocio
│   │   └── resources/
│   │       └── application.properties
├── pom.xml
└── README.md
```

## Validaciones Implementadas

### TaskRequest
- `title`: No puede estar vacío
- `status`: No puede ser nulo
- `priority`: No puede ser nula

### SignupRequest
- `username`: 3-20 caracteres, no vacío
- `email`: Formato de email válido, no vacío
- `password`: 6-40 caracteres, no vacío

### LoginRequest
- `username`: No puede estar vacío
- `password`: No puede estar vacío

## Manejo de Errores

La aplicación maneja los siguientes tipos de errores:

- `ResourceNotFoundException` (404): Recurso no encontrado
- `UnauthorizedException` (401): No autorizado
- `AccessDeniedException` (403): Acceso denegado
- `MethodArgumentNotValidException` (400): Validación fallida
- `Exception` (500): Error interno del servidor

### Ejemplo de Respuesta de Error
```json
{
  "status": 404,
  "message": "Tarea no encontrado con id : '5'",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Conceptos Aplicados

### Programación Funcional
- Uso de Streams para transformar colecciones
- Expresiones lambda en `map()` y `collect()`
- Método `filter()` para filtrar elementos

### Colecciones
- `Set<Role>` para roles de usuario (evita duplicados)
- `List<Task>` para listas de tareas
- `Map<String, Object>` para respuestas dinámicas

### Excepciones
- Excepciones personalizadas: `ResourceNotFoundException`, `UnauthorizedException`
- Manejo global con `@RestControllerAdvice`
- Propagación adecuada de errores

## Testing

### Con Postman
1. Importar la colección incluida: `postman_collection.json`
2. Configurar la variable de entorno `base_url`: `http://localhost:8080`
3. Ejecutar los endpoints en orden:
   - Autenticación (signup/signin)
   - Operaciones CRUD de tareas

### Flujo de Pruebas Recomendado
1. Registrar un nuevo usuario (opcional)
2. Iniciar sesión con `user` o `admin`
3. Copiar el token JWT de la respuesta
4. Usar el token en las peticiones a `/api/tasks`
5. Probar crear, listar, obtener tareas
6. Con usuario ADMIN: probar actualizar y eliminar

## Seguridad

- Contraseñas encriptadas con BCrypt
- Tokens JWT con expiración de 24 horas
- Autenticación basada en tokens
- Autorización a nivel de método con `@PreAuthorize`
- Sesiones stateless (sin estado)

## Autor
Proyecto desarrollado para la asignatura de Ingeniería de Software  
**UNAM - Facultad de Ingeniería**

## Licencia
Este proyecto es de código abierto bajo la licencia Apache 2.0
