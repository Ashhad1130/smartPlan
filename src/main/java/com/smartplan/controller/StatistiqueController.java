package com.smartplan.controller;

import com.smartplan.model.Statistique;
import com.smartplan.model.User;
import com.smartplan.security.JwtUtil;
import com.smartplan.service.StatistiqueService;
import com.smartplan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistiques")
@CrossOrigin(origins = "*")
public class StatistiqueController {
    @Autowired
    private StatistiqueService statistiqueService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/calculate-week")
    public ResponseEntity<?> calculateWeeklyStats(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            Statistique stats = statistiqueService.calculateWeeklyStats(user);
            return ResponseEntity.ok(Map.of("message", "Weekly stats calculated", "statistique", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Statistique> stats = statistiqueService.getUserStats(user);
            return ResponseEntity.ok(Map.of("statistiques", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
