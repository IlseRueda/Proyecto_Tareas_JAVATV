package com.taskmanager.dto;

import com.taskmanager.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    @NotBlank(message = "El título es obligatorio")
    private String title;
    
    private String description;
    
    @NotNull(message = "El estado es obligatorio")
    private Task.TaskStatus status;
    
    @NotNull(message = "La prioridad es obligatoria")
    private Task.Priority priority;
    
    private LocalDateTime dueDate;
}
