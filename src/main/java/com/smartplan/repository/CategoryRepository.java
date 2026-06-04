package com.smartplan.repository;

import com.smartplan.model.Category;
import com.smartplan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);
    boolean existsByUserAndNom(User user, String nom);
}
