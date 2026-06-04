package com.smartplan.service;

import com.smartplan.model.Category;
import com.smartplan.model.User;
import com.smartplan.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public List<Category> getCategoriesByUser(User user) {
        return categoryRepository.findByUser(user);
    }
    
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
    
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }
}
