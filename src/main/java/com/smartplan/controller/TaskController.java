package com.smartplan.controller;

import com.smartplan.model.Task;
import com.smartplan.model.User;
import com.smartplan.security.JwtUtil;
import com.smartplan.service.TaskService;
import com.smartplan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            task.setUser(user);
            
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.ok(Map.of("message", "Task created", "task", createdTask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Task> tasks = taskService.getTasksByUser(user);
            return ResponseEntity.ok(Map.of("tasks", tasks));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task taskDetails, @RequestHeader("Authorization") String token) {
        try {
            Task task = taskService.getTaskById(id);
            if (task == null) {
                return ResponseEntity.notFound().build();
            }
            
            task.setTitre(taskDetails.getTitre());
            task.setDescription(taskDetails.getDescription());
            task.setPriorite(taskDetails.getPriorite());
            task.setStatut(taskDetails.getStatut());
            task.setDateLimite(taskDetails.getDateLimite());
            
            Task updatedTask = taskService.updateTask(task);
            return ResponseEntity.ok(Map.of("message", "Task updated", "task", updatedTask));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok(Map.of("message", "Task deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
