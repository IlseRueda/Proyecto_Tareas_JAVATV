# Arquitectura del Sistema - Task Manager

## Diagrama de Arquitectura General

```mermaid
graph TB
    Client[Cliente/Postman/Swagger] --> Controller[Controllers Layer]
    Controller --> Security[Spring Security + JWT]
    Security --> Service[Services Layer]
    Service --> Repository[Repository Layer]
    Repository --> DB[(MySQL Database)]
    
    Security --> JWT[JWT Utils]
    Security --> UserDetails[UserDetailsService]
    
    Controller --> Exception[Exception Handler]
    
    style Client fill:#e1f5ff
    style Controller fill:#fff4e1
    style Security fill:#ffe1e1
    style Service fill:#e1ffe1
    style Repository fill:#f4e1ff
    style DB fill:#e1e1ff
```

## Diagrama de Capas

```mermaid
graph LR
    A[Presentation Layer<br/>Controllers] --> B[Business Logic Layer<br/>Services]
    B --> C[Persistence Layer<br/>Repositories]
    C --> D[Database Layer<br/>MySQL]
    
    E[Security Layer<br/>JWT + Spring Security] -.-> A
    E -.-> B
    
    F[Exception Handling<br/>Global Handler] -.-> A
    F -.-> B
```

## Flujo de Autenticación

```mermaid
sequenceDiagram
    participant C as Cliente
    participant AC as AuthController
    participant AS as AuthService
    participant SM as SecurityManager
    participant JWT as JwtUtils
    participant DB as Database
    
    C->>AC: POST /api/auth/signin
    AC->>AS: authenticateUser(LoginRequest)
    AS->>SM: authenticate(username, password)
    SM->>DB: findByUsername(username)
    DB-->>SM: User entity
    SM-->>AS: Authentication object
    AS->>JWT: generateJwtToken(authentication)
    JWT-->>AS: JWT token
    AS-->>AC: JwtResponse
    AC-->>C: 200 OK + JWT token
```

## Flujo de Operación CRUD (Crear Tarea)

```mermaid
sequenceDiagram
    participant C as Cliente
    participant F as AuthTokenFilter
    participant TC as TaskController
    participant TS as TaskService
    participant TR as TaskRepository
    participant DB as Database
    
    C->>F: POST /api/tasks + Bearer token
    F->>F: Validar JWT
    F->>F: Cargar UserDetails
    F->>TC: Request con Authentication
    TC->>TC: Validar DTO
    TC->>TS: createTask(TaskRequest)
    TS->>TS: getCurrentUser()
    TS->>TR: save(Task)
    TR->>DB: INSERT INTO tasks
    DB-->>TR: Task entity
    TR-->>TS: Task entity
    TS->>TS: new TaskResponse(task)
    TS-->>TC: TaskResponse
    TC-->>C: 201 Created + TaskResponse
```

## Diagrama de Clases (Modelo de Dominio)

```mermaid
classDiagram
    class User {
        +Long id
        +String username
        +String email
        +String password
        +Set~Role~ roles
        +getAuthorities()
    }
    
    class Role {
        +Long id
        +RoleName name
    }
    
    class Task {
        +Long id
        +String title
        +String description
        +TaskStatus status
        +Priority priority
        +LocalDateTime dueDate
        +User user
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    
    User "1" -- "*" Task : creates
    User "*" -- "*" Role : has
    
    class TaskStatus {
        <<enumeration>>
        PENDING
        IN_PROGRESS
        COMPLETED
        CANCELLED
    }
    
    class Priority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
        URGENT
    }
    
    Task -- TaskStatus
    Task -- Priority
```

## Estructura de Paquetes

```
com.taskmanager
│
├── config
│   ├── SecurityConfig           # Configuración de Spring Security
│   ├── OpenAPIConfig            # Configuración de Swagger
│   └── DataInitializer          # Inicialización de datos
│
├── controller
│   ├── AuthController           # Endpoints de autenticación
│   └── TaskController           # Endpoints CRUD de tareas
│
├── dto
│   ├── LoginRequest             # Request de login
│   ├── SignupRequest            # Request de registro
│   ├── JwtResponse              # Response con token JWT
│   ├── TaskRequest              # Request para crear/actualizar tarea
│   ├── TaskResponse             # Response con datos de tarea
│   └── MessageResponse          # Response genérico de mensaje
│
├── exception
│   ├── ResourceNotFoundException    # Excepción 404
│   ├── UnauthorizedException        # Excepción 401
│   └── GlobalExceptionHandler       # Manejador global de excepciones
│
├── model
│   ├── User                     # Entidad Usuario
│   ├── Role                     # Entidad Rol
│   └── Task                     # Entidad Tarea
│
├── repository
│   ├── UserRepository           # Repositorio de Usuario
│   ├── RoleRepository           # Repositorio de Rol
│   └── TaskRepository           # Repositorio de Tarea
│
├── security
│   ├── JwtUtils                 # Utilidades para JWT
│   ├── AuthTokenFilter          # Filtro de autenticación
│   ├── AuthEntryPointJwt        # Punto de entrada de autenticación
│   └── UserDetailsServiceImpl   # Implementación de UserDetailsService
│
├── service
│   ├── AuthService              # Lógica de autenticación
│   └── TaskService              # Lógica de negocio de tareas
│
└── TaskManagerApplication       # Clase principal
```

## Flujo de Seguridad

```mermaid
graph TD
    A[HTTP Request] --> B{¿Tiene token?}
    B -->|No| C[AuthEntryPoint]
    C --> D[401 Unauthorized]
    
    B -->|Sí| E[AuthTokenFilter]
    E --> F{¿Token válido?}
    F -->|No| C
    
    F -->|Sí| G[Extraer username]
    G --> H[Cargar UserDetails]
    H --> I[Crear Authentication]
    I --> J[SecurityContext]
    J --> K{¿Tiene permisos?}
    
    K -->|No| L[AccessDenied]
    L --> M[403 Forbidden]
    
    K -->|Sí| N[Ejecutar método]
    N --> O[Response exitoso]
```

## Matriz de Permisos

| Operación | Endpoint | USER | ADMIN |
|-----------|----------|------|-------|
| Crear tarea | POST /api/tasks | ✅ | ✅ |
| Listar tareas | GET /api/tasks | ✅ (solo sus tareas) | ✅ (todas) |
| Ver tarea | GET /api/tasks/{id} | ✅ (solo sus tareas) | ✅ |
| Actualizar tarea | PUT /api/tasks/{id} | ❌ | ✅ |
| Eliminar tarea | DELETE /api/tasks/{id} | ❌ | ✅ |
| Registrarse | POST /api/auth/signup | ✅ | ✅ |
| Login | POST /api/auth/signin | ✅ | ✅ |

## Tecnologías por Capa

```mermaid
graph TB
    subgraph "Presentation Layer"
        A1[Spring MVC]
        A2[Jackson JSON]
        A3[Bean Validation]
    end
    
    subgraph "Security Layer"
        B1[Spring Security]
        B2[JWT]
        B3[BCrypt]
    end
    
    subgraph "Business Layer"
        C1[Spring Services]
        C2[Transactions]
        C3[Java Streams]
    end
    
    subgraph "Persistence Layer"
        D1[Spring Data JPA]
        D2[Hibernate]
        D3[MySQL Driver]
    end
    
    subgraph "Documentation"
        E1[Swagger/OpenAPI]
    end
```

## Flujo de Manejo de Errores

```mermaid
graph TD
    A[Exception ocurre] --> B{Tipo de Exception?}
    
    B -->|ResourceNotFoundException| C[404 Handler]
    C --> G[ErrorResponse JSON]
    
    B -->|UnauthorizedException| D[401 Handler]
    D --> G
    
    B -->|AccessDeniedException| E[403 Handler]
    E --> G
    
    B -->|ValidationException| F[400 Handler]
    F --> H[ValidationErrors JSON]
    
    B -->|Otra Exception| I[500 Handler]
    I --> G
    
    G --> J[HTTP Response]
    H --> J
```

## Patrón de Diseño Aplicados

### 1. MVC (Model-View-Controller)
- **Model**: Entidades (User, Task, Role)
- **View**: JSON responses (DTOs)
- **Controller**: RestControllers

### 2. Repository Pattern
- Abstracción de acceso a datos
- Spring Data JPA

### 3. Service Layer Pattern
- Lógica de negocio separada
- Transaccionalidad

### 4. DTO Pattern
- Transferencia de datos
- Separación de modelo interno

### 5. Factory Pattern
- JwtUtils para crear tokens
- UserDetailsService para crear UserDetails

### 6. Chain of Responsibility
- Filtros de Spring Security
- AuthTokenFilter → SecurityFilterChain

### 7. Strategy Pattern
- PasswordEncoder (BCrypt)
- Diferentes implementaciones de autenticación

---

## Base de Datos - Modelo Relacional

```mermaid
erDiagram
    USERS ||--o{ TASKS : creates
    USERS }o--o{ ROLES : has
    
    USERS {
        bigint id PK
        varchar username UK
        varchar email UK
        varchar password
        timestamp created_at
    }
    
    ROLES {
        bigint id PK
        varchar name UK
    }
    
    USER_ROLES {
        bigint user_id FK
        bigint role_id FK
    }
    
    TASKS {
        bigint id PK
        varchar title
        text description
        varchar status
        varchar priority
        timestamp due_date
        bigint user_id FK
        timestamp created_at
        timestamp updated_at
    }
```

## Configuración de Ambientes

```mermaid
graph LR
    A[application.properties] --> B[Development]
    A --> C[Testing]
    A --> D[Production]
    
    B --> B1[H2 Database]
    B --> B2[Port 8080]
    B --> B3[Debug Logs]
    
    C --> C1[MySQL Test]
    C --> C2[Port 8081]
    C --> C3[Test Data]
    
    D --> D1[MySQL Prod]
    D --> D2[Port 80]
    D --> D3[Error Logs Only]
```

## Ciclo de Vida de Request

```mermaid
stateDiagram-v2
    [*] --> Request
    Request --> Filter: HTTP Request
    Filter --> Authentication: Validar JWT
    Authentication --> Authorization: Usuario autenticado
    Authorization --> Controller: Verificar permisos
    Controller --> Validation: Validar DTO
    Validation --> Service: Procesar lógica
    Service --> Repository: Acceder a datos
    Repository --> Database: Query SQL
    Database --> Repository: Result Set
    Repository --> Service: Entity
    Service --> Controller: DTO
    Controller --> Response: JSON
    Response --> [*]
    
    Authentication --> ErrorHandler: Token inválido
    Authorization --> ErrorHandler: Sin permisos
    Validation --> ErrorHandler: Validación fallida
    Service --> ErrorHandler: Lógica error
    Repository --> ErrorHandler: DB error
    ErrorHandler --> Response: Error JSON
```

---

Esta documentación proporciona una visión completa de la arquitectura del sistema Task Manager.
