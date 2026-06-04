package com.smartplan.repository;

import com.smartplan.model.Task;
import com.smartplan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByScorePrioriteDesc(User user);
    List<Task> findByUserAndStatut(User user, Task.Statut statut);
    
    @Query("SELECT t FROM Task t WHERE t.user = ?1 ORDER BY t.scorePriorite DESC")
    List<Task> findTasksOrderedByPriority(User user);
}
