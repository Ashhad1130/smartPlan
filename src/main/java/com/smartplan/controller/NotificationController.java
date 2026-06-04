package com.smartplan.controller;

import com.smartplan.model.Notification;
import com.smartplan.model.User;
import com.smartplan.security.JwtUtil;
import com.smartplan.service.NotificationService;
import com.smartplan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Notification> notifications = notificationService.getUnreadNotifications(user);
            return ResponseEntity.ok(Map.of("notifications", notifications, "count", notifications.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllNotifications(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Notification> notifications = notificationService.getAllNotifications(user);
            return ResponseEntity.ok(Map.of("notifications", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("message", "Notification deleted"));
    }
}
