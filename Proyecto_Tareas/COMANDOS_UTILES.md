# Comandos Útiles - Task Manager

## Maven

### Compilar el proyecto
```bash
mvn clean compile
```

### Ejecutar tests
```bash
mvn test
```

### Empaquetar en JAR
```bash
mvn clean package
```

### Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### Ejecutar con perfil específico
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Saltar tests al empaquetar
```bash
mvn clean package -DskipTests
```

---

## MySQL

### Conectarse a MySQL
```bash
mysql -u root -p
```

### Crear base de datos
```sql
CREATE DATABASE taskmanager_db;
```

### Ver bases de datos
```sql
SHOW DATABASES;
```

### Usar base de datos
```sql
USE taskmanager_db;
```

### Ver tablas
```sql
SHOW TABLES;
```

### Describir estructura de tabla
```sql
DESCRIBE tasks;
DESCRIBE users;
DESCRIBE roles;
```

### Ver todos los roles
```sql
SELECT * FROM roles;
```

### Ver usuarios con roles
```sql
SELECT u.username, u.email, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;
```

### Ver todas las tareas
```sql
SELECT t.id, t.title, t.status, t.priority, u.username, t.created_at
FROM tasks t
JOIN users u ON t.user_id = u.id
ORDER BY t.created_at DESC;
```

### Eliminar todas las tareas
```sql
DELETE FROM tasks;
```

### Resetear auto_increment
```sql
ALTER TABLE tasks AUTO_INCREMENT = 1;
```

### Backup de la base de datos
```bash
mysqldump -u root -p taskmanager_db > backup.sql
```

### Restaurar backup
```bash
mysql -u root -p taskmanager_db < backup.sql
```

---

## cURL - Pruebas de API

### Registrar usuario
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "roles": ["user"]
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### Crear tarea (requiere token)
```bash
TOKEN="tu-token-jwt-aqui"

curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Nueva tarea",
    "description": "Descripción de la tarea",
    "status": "PENDING",
    "priority": "HIGH",
    "dueDate": "2024-12-31T23:59:59"
  }'
```

### Listar todas las tareas
```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener tarea por ID
```bash
curl -X GET http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar tarea
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Tarea actualizada",
    "description": "Nueva descripción",
    "status": "COMPLETED",
    "priority": "MEDIUM",
    "dueDate": "2024-12-31T23:59:59"
  }'
```

### Eliminar tarea
```bash
curl -X DELETE http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Git

### Inicializar repositorio
```bash
git init
```

### Agregar archivos
```bash
git add .
```

### Commit
```bash
git commit -m "Initial commit - Task Manager API"
```

### Agregar remote
```bash
git remote add origin https://github.com/tu-usuario/task-manager.git
```

### Push
```bash
git push -u origin main
```

### Ver status
```bash
git status
```

### Ver historial
```bash
git log --oneline
```

### Crear rama
```bash
git checkout -b feature/nueva-funcionalidad
```

### Cambiar de rama
```bash
git checkout main
```

### Mergear rama
```bash
git merge feature/nueva-funcionalidad
```

---

## Docker (Opcional)

### Crear Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/task-manager-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Build imagen
```bash
docker build -t task-manager:1.0 .
```

### Ejecutar contenedor
```bash
docker run -p 8080:8080 task-manager:1.0
```

### Docker Compose con MySQL
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: taskmanager_db
    ports:
      - "3306:3306"
  
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/taskmanager_db
```

---

## Logs

### Ver logs en tiempo real
```bash
tail -f logs/spring.log
```

### Buscar errores en logs
```bash
grep "ERROR" logs/spring.log
```

### Ver últimas 100 líneas
```bash
tail -n 100 logs/spring.log
```

---

## Testing

### Ejecutar todos los tests
```bash
mvn test
```

### Ejecutar test específico
```bash
mvn test -Dtest=TaskServiceTest
```

### Ver reporte de cobertura
```bash
mvn jacoco:report
```

---

## Postman

### Importar colección
1. Abrir Postman
2. File → Import
3. Seleccionar `postman_collection.json`

### Variables de entorno
```json
{
  "base_url": "http://localhost:8080",
  "token": ""
}
```

### Pre-request Script para auto-token
```javascript
// En la carpeta "Tasks", Pre-request Script:
const loginRequest = {
  url: pm.environment.get("base_url") + "/api/auth/signin",
  method: 'POST',
  header: {
    'Content-Type': 'application/json',
  },
  body: {
    mode: 'raw',
    raw: JSON.stringify({
      username: "admin",
      password: "admin123"
    })
  }
};

pm.sendRequest(loginRequest, function (err, response) {
  if (!err) {
    const token = response.json().token;
    pm.environment.set("token", token);
  }
});
```

---

## IntelliJ IDEA

### Atajos útiles
- `Ctrl + Space`: Autocompletado
- `Ctrl + Shift + F`: Buscar en proyecto
- `Ctrl + Alt + L`: Formatear código
- `Shift + F10`: Ejecutar aplicación
- `Ctrl + F9`: Build proyecto

### Plugins recomendados
- Lombok
- Spring Boot Assistant
- Database Navigator
- GitToolBox

---

## VS Code

### Extensiones recomendadas
- Extension Pack for Java
- Spring Boot Extension Pack
- MySQL
- REST Client

### Configuración
```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "spring-boot.ls.problem.application-properties.PROP_UNKNOWN_PROPERTY": "WARNING"
}
```

---

## Troubleshooting

### Puerto 8080 ocupado
```bash
# Linux/Mac
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Limpiar caché de Maven
```bash
mvn dependency:purge-local-repository
```

### Regenerar IDE files
```bash
mvn idea:idea      # IntelliJ
mvn eclipse:eclipse # Eclipse
```

### Ver versión de Java
```bash
java -version
javac -version
```

### Verificar instalación de Maven
```bash
mvn -version
```

---

## Producción

### Crear JAR ejecutable
```bash
mvn clean package -DskipTests
```

### Ejecutar JAR
```bash
java -jar target/task-manager-1.0.0.jar
```

### Con perfil de producción
```bash
java -jar target/task-manager-1.0.0.jar --spring.profiles.active=prod
```

### Variables de entorno
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://prod-db:3306/taskmanager_db
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export JWT_SECRET=your-secure-secret-key

java -jar target/task-manager-1.0.0.jar
```

---

## Monitoring

### Actuator endpoints (si se añade)
```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Info
curl http://localhost:8080/actuator/info
```

---

## Backup y Restauración

### Backup completo
```bash
# Base de datos
mysqldump -u root -p taskmanager_db > backup_$(date +%Y%m%d).sql

# Archivos del proyecto
tar -czf task-manager-backup.tar.gz task-manager/
```

### Restauración
```bash
# Base de datos
mysql -u root -p taskmanager_db < backup_20240115.sql

# Archivos
tar -xzf task-manager-backup.tar.gz
```

---

## Performance

### Ver threads en ejecución
```bash
jps -l
jstack <PID>
```

### Memory dump
```bash
jmap -dump:format=b,file=heap.bin <PID>
```

### GC logs
```bash
java -Xlog:gc* -jar target/task-manager-1.0.0.jar
```
