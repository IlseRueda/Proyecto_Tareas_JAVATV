package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public TaskResponse createTask(TaskRequest taskRequest) {
        User currentUser = getCurrentUser();
        
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        task.setDueDate(taskRequest.getDueDate());
        task.setUser(currentUser);
        
        Task savedTask = taskRepository.save(task);
        return new TaskResponse(savedTask);
    }
    
    public List<TaskResponse> getAllTasks() {
        User currentUser = getCurrentUser();
        
        // Si es ADMIN, puede ver todas las tareas
        if (isAdmin()) {
            return taskRepository.findAll().stream()
                    .map(TaskResponse::new)
                    .collect(Collectors.toList());
        }
        
        // Si es USER, solo ve sus propias tareas
        return taskRepository.findByUser(currentUser).stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
    
    public TaskResponse getTaskById(Long id) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        // Verificar que el usuario tiene permiso para ver esta tarea
        if (!isAdmin() && !task.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("No tienes permiso para ver esta tarea");
        }
        
        return new TaskResponse(task);
    }
    
    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        // Solo ADMIN puede actualizar cualquier tarea
        if (!isAdmin() && !task.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("No tienes permiso para actualizar esta tarea");
        }
        
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        task.setDueDate(taskRequest.getDueDate());
        
        Task updatedTask = taskRepository.save(task);
        return new TaskResponse(updatedTask);
    }
    
    public void deleteTask(Long id) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", "id", id));
        
        // Solo ADMIN puede eliminar cualquier tarea
        if (!isAdmin() && !task.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("No tienes permiso para eliminar esta tarea");
        }
        
        taskRepository.delete(task);
    }
    
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
    
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
