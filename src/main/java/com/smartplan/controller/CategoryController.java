package com.smartplan.controller;

import com.smartplan.model.Category;
import com.smartplan.model.User;
import com.smartplan.security.JwtUtil;
import com.smartplan.service.CategoryService;
import com.smartplan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category, @RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            category.setUser(user);
            
            Category created = categoryService.createCategory(category);
            return ResponseEntity.ok(Map.of("message", "Category created", "category", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getCategories(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token.replace("Bearer ", ""));
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Category> categories = categoryService.getCategoriesByUser(user);
            return ResponseEntity.ok(Map.of("categories", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message", "Category deleted"));
    }
}
