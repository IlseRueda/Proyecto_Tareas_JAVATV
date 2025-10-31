-- Script de inicialización de base de datos para Task Manager
-- MySQL 8.0+

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS taskmanager_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE taskmanager_db;

-- Las tablas se crearán automáticamente por Hibernate con spring.jpa.hibernate.ddl-auto=update
-- Este script es solo para referencia de la estructura

-- Tabla: roles
-- CREATE TABLE roles (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(50) NOT NULL UNIQUE
-- );

-- Tabla: users
-- CREATE TABLE users (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(50) NOT NULL UNIQUE,
--     email VARCHAR(100) NOT NULL UNIQUE,
--     password VARCHAR(255) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- Tabla: user_roles (relación muchos a muchos)
-- CREATE TABLE user_roles (
--     user_id BIGINT NOT NULL,
--     role_id BIGINT NOT NULL,
--     PRIMARY KEY (user_id, role_id),
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
--     FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
-- );

-- Tabla: tasks
-- CREATE TABLE tasks (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     title VARCHAR(255) NOT NULL,
--     description TEXT,
--     status VARCHAR(50) NOT NULL,
--     priority VARCHAR(50) NOT NULL,
--     due_date TIMESTAMP,
--     user_id BIGINT NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
-- );

-- Índices para mejorar el rendimiento
-- CREATE INDEX idx_tasks_user_id ON tasks(user_id);
-- CREATE INDEX idx_tasks_status ON tasks(status);
-- CREATE INDEX idx_tasks_priority ON tasks(priority);
-- CREATE INDEX idx_tasks_due_date ON tasks(due_date);

-- Los datos iniciales (roles y usuarios) se insertan automáticamente
-- mediante la clase DataInitializer.java

-- Consultas útiles para verificar los datos

-- Ver todos los roles
SELECT * FROM roles;

-- Ver todos los usuarios con sus roles
SELECT u.id, u.username, u.email, r.name as role
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;

-- Ver todas las tareas con información del usuario
SELECT t.id, t.title, t.status, t.priority, u.username, t.created_at
FROM tasks t
JOIN users u ON t.user_id = u.id
ORDER BY t.created_at DESC;

-- Contar tareas por estado
SELECT status, COUNT(*) as count
FROM tasks
GROUP BY status;

-- Contar tareas por usuario
SELECT u.username, COUNT(t.id) as task_count
FROM users u
LEFT JOIN tasks t ON u.id = t.user_id
GROUP BY u.id, u.username;
