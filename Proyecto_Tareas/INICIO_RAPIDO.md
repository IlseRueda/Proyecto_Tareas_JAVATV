# 🚀 Inicio Rápido - Task Manager

## ⚡ En 5 Minutos

### Paso 1: Crear Base de Datos (1 min)
```bash
mysql -u root -p
```
```sql
CREATE DATABASE taskmanager_db;
exit;
```

### Paso 2: Configurar Credenciales (30 seg)
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

### Paso 3: Compilar y Ejecutar (3 min)
```bash
cd task-manager
mvn clean install
mvn spring-boot:run
```

### Paso 4: Probar (30 seg)
Abrir: http://localhost:8080/swagger-ui.html

---

## 🎯 Primera Prueba

### 1. Login como Admin
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2. Copiar el Token
De la respuesta, copiar el valor de `"token"`: `"eyJhbGc..."`

### 3. Crear una Tarea
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{
    "title": "Mi primera tarea",
    "description": "Probando la API",
    "status": "PENDING",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59"
  }'
```

### 4. Ver Todas las Tareas
```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## 🔑 Credenciales de Prueba

| Usuario | Password | Rol | Permisos |
|---------|----------|-----|----------|
| `admin` | `admin123` | ADMIN | Todos |
| `user` | `user123` | USER | Solo lectura y crear |

---

## 📍 URLs Importantes

| Servicio | URL |
|----------|-----|
| **API Base** | http://localhost:8080/api |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **API Docs** | http://localhost:8080/api-docs |
| **Health Check** | http://localhost:8080/actuator/health |

---

## 🛠️ Solución de Problemas Rápidos

### ❌ Puerto 8080 ocupado
```properties
# En application.properties cambiar:
server.port=8081
```

### ❌ Error de conexión a MySQL
1. Verificar que MySQL está corriendo
2. Verificar usuario y contraseña
3. Verificar que existe la base de datos

### ❌ Token inválido o expirado
1. Hacer login nuevamente
2. Copiar el nuevo token
3. El token expira en 24 horas

### ❌ Maven build falla
```bash
mvn clean
mvn dependency:resolve
mvn clean install
```

---

## 📊 Endpoints Principales

### Autenticación
```
POST /api/auth/signin   - Login
POST /api/auth/signup   - Registro
```

### Tareas
```
GET    /api/tasks       - Listar todas
GET    /api/tasks/{id}  - Ver una tarea
POST   /api/tasks       - Crear tarea
PUT    /api/tasks/{id}  - Actualizar (ADMIN)
DELETE /api/tasks/{id}  - Eliminar (ADMIN)
```

---

## 🎓 Siguiente Paso

Una vez que funcione:

1. Lee **README.md** para documentación completa
2. Revisa **ARQUITECTURA.md** para entender el diseño
3. Lee **CONCEPTOS_APLICADOS.md** para aprender los conceptos
4. Sigue **TESTING_GUIDE.md** para hacer todas las pruebas

---

## 📞 Ayuda

Si algo no funciona:
1. Revisa **COMANDOS_UTILES.md** sección Troubleshooting
2. Verifica logs en la consola
3. Asegúrate de tener Java 17+

---

**¡Listo! Ya tienes tu API funcionando** 🎉

Para más información detallada, consulta **README.md**
