# Conceptos de Programación Aplicados en Task Manager

## 1. Programación Funcional

### 1.1 Streams y Lambdas
El proyecto hace uso extensivo de Streams para procesar colecciones de manera funcional.

**Ejemplo en TaskService.java:**
```java
public List<TaskResponse> getAllTasks() {
    return taskRepository.findAll().stream()
            .map(TaskResponse::new)  // Lambda: task -> new TaskResponse(task)
            .collect(Collectors.toList());
}
```

**Explicación:**
- `stream()`: Convierte la lista en un Stream para procesamiento funcional
- `map()`: Transforma cada Task en un TaskResponse
- `TaskResponse::new`: Method reference (referencia a método)
- `collect()`: Recolecta los resultados en una nueva lista

### 1.2 Method References
Uso de referencias a métodos en lugar de lambdas explícitas:

```java
// En lugar de: .map(task -> new TaskResponse(task))
.map(TaskResponse::new)

// En lugar de: .map(role -> role.getName().name())
.map(role -> new SimpleGrantedAuthority(role.getName().name()))
```

### 1.3 Operaciones de Filtrado
**Ejemplo en TaskService.java:**
```java
private boolean isAdmin() {
    return SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
}
```

**Explicación:**
- `anyMatch()`: Operación terminal que verifica si algún elemento cumple la condición
- Lambda: `auth -> auth.getAuthority().equals("ROLE_ADMIN")`
- Retorna true si encuentra al menos un elemento que coincide

### 1.4 Collectors
**Ejemplo en AuthService.java:**
```java
List<String> roles = user.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());
```

**Otros Collectors disponibles:**
- `Collectors.toSet()`: Para evitar duplicados
- `Collectors.joining()`: Para concatenar strings
- `Collectors.groupingBy()`: Para agrupar elementos

---

## 2. Colecciones

### 2.1 Set (HashSet)
**Ejemplo en User.java:**
```java
@ManyToMany(fetch = FetchType.EAGER)
private Set<Role> roles = new HashSet<>();
```

**¿Por qué Set?**
- No permite duplicados (un usuario no puede tener el mismo rol dos veces)
- No mantiene orden específico
- Búsqueda más eficiente que List (O(1) vs O(n))

**Operaciones:**
```java
Set<Role> roles = new HashSet<>();
roles.add(adminRole);      // Agregar
roles.contains(userRole);  // Verificar existencia
roles.remove(oldRole);     // Eliminar
```

### 2.2 List (ArrayList)
**Ejemplo en TaskService.java:**
```java
List<TaskResponse> tasks = taskRepository.findAll().stream()
        .map(TaskResponse::new)
        .collect(Collectors.toList());
```

**¿Por qué List?**
- Mantiene el orden de inserción
- Permite duplicados
- Acceso por índice: `list.get(0)`

**Operaciones:**
```java
List<Task> tasks = new ArrayList<>();
tasks.add(task);           // Agregar al final
tasks.get(0);              // Acceder por índice
tasks.size();              // Tamaño
tasks.isEmpty();           // Verificar si está vacío
```

### 2.3 Map (HashMap)
**Ejemplo en GlobalExceptionHandler.java:**
```java
Map<String, String> errors = new HashMap<>();
ex.getBindingResult().getAllErrors().forEach((error) -> {
    String fieldName = ((FieldError) error).getField();
    String errorMessage = error.getDefaultMessage();
    errors.put(fieldName, errorMessage);
});
```

**¿Por qué Map?**
- Almacena pares clave-valor
- Búsqueda rápida por clave: O(1)
- Ideal para respuestas dinámicas de API

**Operaciones:**
```java
Map<String, Object> response = new HashMap<>();
response.put("status", 200);       // Agregar
response.get("status");            // Obtener
response.containsKey("message");   // Verificar clave
response.remove("error");          // Eliminar
```

---

## 3. Excepciones

### 3.1 Excepciones Personalizadas

**ResourceNotFoundException.java:**
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s : '%s'", 
              resourceName, fieldName, fieldValue));
    }
}
```

**UnauthorizedException.java:**
```java
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
```

### 3.2 Lanzamiento de Excepciones
```java
// En TaskService.java
Task task = taskRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));

if (!isAdmin() && !task.getUser().getId().equals(currentUser.getId())) {
    throw new UnauthorizedException("No tienes permiso para ver esta tarea");
}
```

**Explicación:**
- `orElseThrow()`: Método funcional de Optional
- Lambda: `() -> new ResourceNotFoundException(...)`
- `throw`: Lanza la excepción explícitamente

### 3.3 Manejo Global de Excepciones
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

**Ventajas:**
- Centraliza el manejo de errores
- Respuestas consistentes en toda la API
- Reduce código repetitivo en controladores

### 3.4 Jerarquía de Excepciones
```
Throwable
└── Exception
    └── RuntimeException (unchecked)
        ├── ResourceNotFoundException (custom)
        ├── UnauthorizedException (custom)
        ├── IllegalArgumentException
        └── NullPointerException
```

**Checked vs Unchecked:**
- **Checked**: Deben declararse o capturarse (IOException, SQLException)
- **Unchecked**: No requieren declaración (RuntimeException y sus subclases)
- Este proyecto usa **Unchecked** (heredan de RuntimeException)

---

## 4. Optional

### 4.1 Uso en Repositorios
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

### 4.2 Manejo de Optional
```java
// Forma 1: orElseThrow
User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado"));

// Forma 2: ifPresent
roleRepository.findByName(Role.RoleName.ROLE_USER)
        .ifPresent(role -> roles.add(role));

// Forma 3: orElse (valor por defecto)
String username = userOptional
        .map(User::getUsername)
        .orElse("anonymous");
```

**Ventajas de Optional:**
- Evita NullPointerException
- Hace explícito que un valor puede no existir
- API funcional para manipular valores

---

## 5. Inmutabilidad y Records (Conceptos Modernos)

Aunque no usamos Records en este proyecto (para mantener compatibilidad), el concepto está presente:

**DTOs como clases inmutables:**
```java
@Data  // Genera getters, setters, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    // ...
}
```

**Con Java 17+ se podría usar:**
```java
public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status
) {}
```

---

## 6. Composition over Inheritance

El proyecto favorece la composición sobre la herencia:

```java
@Entity
public class Task {
    @ManyToOne
    private User user;  // Composición: Task "tiene un" User
}

@Entity
public class User implements UserDetails {
    @ManyToMany
    private Set<Role> roles;  // Composición: User "tiene muchos" Roles
}
```

**En lugar de:**
```java
public class AdminUser extends User { }  // Herencia (evitada)
```

---

## 7. Dependency Injection

### 7.1 Inyección por Constructor (Recomendada)
Aunque usamos `@Autowired` en campos, la forma moderna es:

```java
@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
}
```

### 7.2 Ventajas de DI
- Bajo acoplamiento
- Fácil de testear (mocks)
- Facilita el cambio de implementaciones

---

## 8. Transacciones

```java
@Service
@Transactional  // Todas las operaciones en transacción
public class TaskService {
    
    public TaskResponse createTask(TaskRequest taskRequest) {
        // Si hay error, se hace rollback automático
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        return new TaskResponse(taskRepository.save(task));
    }
}
```

**Características:**
- Atomicidad: Todo o nada
- Consistencia: Estado válido de BD
- Rollback automático en excepciones

---

## 9. Builder Pattern (Conceptual)

Aunque no implementado explícitamente, se podría mejorar con:

```java
Task task = Task.builder()
        .title("Mi tarea")
        .description("Descripción")
        .status(TaskStatus.PENDING)
        .priority(Priority.HIGH)
        .user(currentUser)
        .build();
```

Lombok permite esto con `@Builder` en la entidad.

---

## 10. Strategy Pattern

Aplicado implícitamente en Spring Security:

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
}

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // Estrategia específica de carga de usuarios
}
```

---

## Resumen de Conceptos

| Concepto | Ubicación | Beneficio |
|----------|-----------|-----------|
| Streams | TaskService, AuthService | Código más limpio y funcional |
| Set | User.roles | Evita roles duplicados |
| List | Respuestas de API | Mantiene orden de tareas |
| Map | Manejo de errores | Respuestas dinámicas |
| Optional | Repositorios | Evita NullPointerException |
| Excepciones Personalizadas | exception/ | Mejor manejo de errores |
| @RestControllerAdvice | GlobalExceptionHandler | Manejo centralizado |
| Dependency Injection | Todos los servicios | Bajo acoplamiento |
| @Transactional | Services | Integridad de datos |
| Lambda Expressions | Everywhere | Código conciso |

---

## Buenas Prácticas Aplicadas

1. **Separación de capas:** Controller → Service → Repository
2. **DTOs:** No exponer entidades directamente en APIs
3. **Validaciones:** Bean Validation en DTOs
4. **Seguridad:** JWT + Spring Security
5. **Documentación:** Swagger/OpenAPI
6. **Manejo de errores:** Centralizado y consistente
7. **Nomenclatura:** Camel case, nombres descriptivos
8. **Comentarios:** Solo donde añaden valor
9. **SOLID:** Responsabilidad única, interfaces
10. **DRY:** Don't Repeat Yourself

---

## Para Profundizar

**Programación Funcional:**
- Libro: "Functional Programming in Java" - Venkat Subramaniam
- Stream API Javadoc

**Colecciones:**
- Java Collections Framework
- Guía de Google Collections

**Patrones de Diseño:**
- "Design Patterns" - Gang of Four
- "Head First Design Patterns"

**Spring Boot:**
- Documentación oficial: spring.io
- Baeldung.com tutoriales
