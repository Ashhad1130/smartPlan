package com.smartplan.service;

import com.smartplan.model.Task;
import com.smartplan.model.User;
import com.smartplan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    
    public Task createTask(Task task) {
        calculatePriority(task);
        return taskRepository.save(task);
    }
    
    public List<Task> getTasksByUser(User user) {
        return taskRepository.findTasksOrderedByPriority(user);
    }
    
    public Task updateTask(Task task) {
        calculatePriority(task);
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
    
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }
    
    private void calculatePriority(Task task) {
        int poidsPriorite = getPoidsPriorite(task.getPriorite());
        long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDate.now(), task.getDateLimite());
        int urgenceDelai = getUrgenceDelai(daysUntilDeadline);
        
        int score = (poidsPriorite * 40) + (urgenceDelai * 60);
        task.setScorePriorite(score);
    }
    
    private int getPoidsPriorite(Task.Priorite priorite) {
        return switch(priorite) {
            case HAUTE -> 3;
            case MOYENNE -> 2;
            case BASSE -> 1;
        };
    }
    
    private int getUrgenceDelai(long daysUntilDeadline) {
        if (daysUntilDeadline < 1) return 3;
        if (daysUntilDeadline <= 3) return 2;
        return 1;
    }
}
