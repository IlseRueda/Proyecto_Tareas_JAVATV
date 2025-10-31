# Guía de Pruebas - Task Manager API

## Configuración Inicial

### 1. Base de Datos MySQL
Asegúrate de tener MySQL corriendo y crear la base de datos:
```sql
CREATE DATABASE taskmanager_db;
```

### 2. Iniciar la Aplicación
```bash
mvn spring-boot:run
```

Deberías ver en la consola:
```
Roles creados exitosamente
Usuarios de prueba creados:
ADMIN - username: admin, password: admin123
USER - username: user, password: user123
```

## Pruebas con Postman

### Test 1: Registrar un Nuevo Usuario
**Endpoint:** POST `/api/auth/signup`

**Body:**
```json
{
  "username": "sebastian",
  "email": "sebastian@unam.mx",
  "password": "password123",
  "roles": ["user"]
}
```

**Resultado Esperado:** 201 Created
```json
{
  "message": "Usuario registrado exitosamente"
}
```

**Captura:** `screenshots/01-signup-success.png`
- Status: 201 Created
- Response con mensaje de éxito

---

### Test 2: Login como Usuario ADMIN
**Endpoint:** POST `/api/auth/signin`

**Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Resultado Esperado:** 200 OK
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@taskmanager.com",
  "roles": ["ROLE_ADMIN"]
}
```

**Captura:** `screenshots/02-login-admin-success.png`
- Status: 200 OK
- Response con token JWT
- Guardar el token para las siguientes pruebas

---

### Test 3: Crear Tarea (como USER)
**Endpoint:** POST `/api/tasks`

**Headers:**
```
Authorization: Bearer {token-del-user}
Content-Type: application/json
```

**Body:**
```json
{
  "title": "Estudiar para examen de Spring Boot",
  "description": "Repasar conceptos de JPA, Security y REST",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2024-12-15T18:00:00"
}
```

**Resultado Esperado:** 201 Created
```json
{
  "id": 1,
  "title": "Estudiar para examen de Spring Boot",
  "description": "Repasar conceptos de JPA, Security y REST",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2024-12-15T18:00:00",
  "username": "user",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Captura:** `screenshots/03-create-task-user.png`
- Status: 201 Created
- Task creada con todos los campos
- Muestra username del creador

---

### Test 4: Listar Todas las Tareas (como USER)
**Endpoint:** GET `/api/tasks`

**Headers:**
```
Authorization: Bearer {token-del-user}
```

**Resultado Esperado:** 200 OK
```json
[
  {
    "id": 1,
    "title": "Estudiar para examen de Spring Boot",
    "status": "PENDING",
    "priority": "HIGH",
    "username": "user",
    ...
  }
]
```

**Nota:** El usuario USER solo ve sus propias tareas.

**Captura:** `screenshots/04-get-all-tasks-user.png`
- Status: 200 OK
- Array con tareas del usuario actual

---

### Test 5: Intentar Actualizar Tarea (como USER) - DEBE FALLAR
**Endpoint:** PUT `/api/tasks/1`

**Headers:**
```
Authorization: Bearer {token-del-user}
```

**Body:**
```json
{
  "title": "Tarea actualizada",
  "description": "Intento de actualización",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2024-12-20T18:00:00"
}
```

**Resultado Esperado:** 403 Forbidden
```json
{
  "status": 403,
  "message": "No tienes permisos para realizar esta acción",
  "timestamp": "2024-01-15T10:35:00"
}
```

**Captura:** `screenshots/05-update-task-user-forbidden.png`
- Status: 403 Forbidden
- Mensaje de error de permisos
- Demuestra que USER no puede actualizar

---

### Test 6: Login como ADMIN
**Endpoint:** POST `/api/auth/signin`

**Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Resultado Esperado:** 200 OK con token de ADMIN

**Captura:** `screenshots/06-login-admin.png`
- Status: 200 OK
- Token JWT de administrador
- Roles: ["ROLE_ADMIN"]

---

### Test 7: Listar Todas las Tareas (como ADMIN)
**Endpoint:** GET `/api/tasks`

**Headers:**
```
Authorization: Bearer {token-del-admin}
```

**Resultado Esperado:** 200 OK
```json
[
  {
    "id": 1,
    "title": "Estudiar para examen de Spring Boot",
    "username": "user",
    ...
  },
  {
    "id": 2,
    "title": "Otra tarea de otro usuario",
    "username": "sebastian",
    ...
  }
]
```

**Nota:** El ADMIN ve TODAS las tareas de todos los usuarios.

**Captura:** `screenshots/07-get-all-tasks-admin.png`
- Status: 200 OK
- Array con todas las tareas del sistema
- Muestra tareas de diferentes usuarios

---

### Test 8: Actualizar Tarea (como ADMIN) - DEBE FUNCIONAR
**Endpoint:** PUT `/api/tasks/1`

**Headers:**
```
Authorization: Bearer {token-del-admin}
Content-Type: application/json
```

**Body:**
```json
{
  "title": "Examen de Spring Boot - Completado",
  "description": "Todos los temas revisados y aprobados",
  "status": "COMPLETED",
  "priority": "MEDIUM",
  "dueDate": "2024-12-15T18:00:00"
}
```

**Resultado Esperado:** 200 OK
```json
{
  "id": 1,
  "title": "Examen de Spring Boot - Completado",
  "description": "Todos los temas revisados y aprobados",
  "status": "COMPLETED",
  "priority": "MEDIUM",
  "dueDate": "2024-12-15T18:00:00",
  "username": "user",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:40:00"
}
```

**Captura:** `screenshots/08-update-task-admin-success.png`
- Status: 200 OK
- Task actualizada con nuevos valores
- updatedAt diferente de createdAt

---

### Test 9: Eliminar Tarea (como ADMIN)
**Endpoint:** DELETE `/api/tasks/1`

**Headers:**
```
Authorization: Bearer {token-del-admin}
```

**Resultado Esperado:** 200 OK
```json
{
  "message": "Tarea eliminada exitosamente"
}
```

**Captura:** `screenshots/09-delete-task-admin-success.png`
- Status: 200 OK
- Mensaje de confirmación

---

### Test 10: Intentar Acceder sin Token - DEBE FALLAR
**Endpoint:** GET `/api/tasks`

**Headers:** (SIN Authorization header)

**Resultado Esperado:** 401 Unauthorized
```json
{
  "status": 401,
  "error": "No autorizado",
  "message": "Full authentication is required to access this resource",
  "path": "/api/tasks"
}
```

**Captura:** `screenshots/10-no-token-unauthorized.png`
- Status: 401 Unauthorized
- Mensaje de autenticación requerida

---

### Test 11: Validación de Campos Requeridos
**Endpoint:** POST `/api/tasks`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:** (con campos vacíos)
```json
{
  "title": "",
  "description": "Sin título",
  "status": null,
  "priority": null
}
```

**Resultado Esperado:** 400 Bad Request
```json
{
  "status": 400,
  "errors": {
    "title": "El título es obligatorio",
    "status": "El estado es obligatorio",
    "priority": "La prioridad es obligatoria"
  },
  "timestamp": "2024-01-15T10:45:00"
}
```

**Captura:** `screenshots/11-validation-errors.png`
- Status: 400 Bad Request
- Errores de validación detallados por campo

---

## Pruebas con Swagger UI

### Acceder a Swagger
1. Abrir navegador en: `http://localhost:8080/swagger-ui.html`
2. Click en "Authorize" (candado verde)
3. Ingresar: `Bearer {tu-token-jwt}`
4. Click en "Authorize"

**Captura:** `screenshots/12-swagger-authorize.png`
- Interfaz de Swagger UI
- Modal de autorización
- Token ingresado

### Probar Endpoints desde Swagger
**Captura:** `screenshots/13-swagger-try-endpoint.png`
- Endpoint expandido
- Botón "Try it out"
- Request body editable
- Response con status code y body

---

## Validaciones de Seguridad Probadas

✅ Usuario no autenticado no puede acceder a endpoints protegidos  
✅ Token JWT requerido para todas las operaciones de tareas  
✅ Usuario USER solo puede crear y ver sus tareas  
✅ Usuario USER NO puede actualizar ni eliminar tareas  
✅ Usuario ADMIN puede realizar todas las operaciones CRUD  
✅ Usuario ADMIN puede ver tareas de todos los usuarios  
✅ Validaciones de campos obligatorios funcionan correctamente  
✅ Manejo de errores apropiado (404, 401, 403, 400, 500)

---

## Checklist de Pruebas Completadas

- [x] Registro de usuario
- [x] Login de usuario
- [x] Login de administrador
- [x] Crear tarea como USER
- [x] Crear tarea como ADMIN
- [x] Listar tareas como USER (solo ve las suyas)
- [x] Listar tareas como ADMIN (ve todas)
- [x] Obtener tarea por ID
- [x] Actualizar tarea como USER (debe fallar)
- [x] Actualizar tarea como ADMIN (debe funcionar)
- [x] Eliminar tarea como USER (debe fallar)
- [x] Eliminar tarea como ADMIN (debe funcionar)
- [x] Acceso sin token (debe fallar)
- [x] Validaciones de campos
- [x] Documentación Swagger funcional

---

## Notas Adicionales

### Estados de Tarea Probados
- PENDING
- IN_PROGRESS
- COMPLETED
- CANCELLED

### Prioridades Probadas
- LOW
- MEDIUM
- HIGH
- URGENT

### Tipos de Error Verificados
- 400 Bad Request (validaciones)
- 401 Unauthorized (sin token)
- 403 Forbidden (sin permisos)
- 404 Not Found (recurso no existe)
- 500 Internal Server Error (errores del servidor)

---

## Conclusión
Todas las funcionalidades CRUD han sido probadas exitosamente.
El sistema de autenticación y autorización funciona correctamente.
Las validaciones y el manejo de errores están implementados apropiadamente.
La documentación con Swagger está disponible y funcional.
