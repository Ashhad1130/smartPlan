package com.smartplan.service;

import com.smartplan.model.Statistique;
import com.smartplan.model.Task;
import com.smartplan.model.User;
import com.smartplan.repository.StatistiqueRepository;
import com.smartplan.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class StatistiqueService {
    @Autowired
    private StatistiqueRepository statistiqueRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    public Statistique calculateWeeklyStats(User user) {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        LocalDate weekStart = today.with(weekFields.dayOfWeek(), 1);
        
        List<Task> allTasks = taskRepository.findByUserAndStatut(user, Task.Statut.TERMINE);
        long completedCount = allTasks.stream()
            .filter(t -> t.getCreatedAt().toLocalDate().isAfter(weekStart) && 
                        t.getCreatedAt().toLocalDate().isBefore(today.plusDays(1)))
            .count();
        
        long totalCount = taskRepository.findByUserAndStatut(user, Task.Statut.A_FAIRE).size() + completedCount;
        
        BigDecimal completionRate = totalCount > 0 
            ? BigDecimal.valueOf((completedCount * 100.0) / totalCount).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        Statistique stat = new Statistique();
        stat.setUser(user);
        stat.setSemaine(weekStart);
        stat.setTauxCompletion(completionRate);
        stat.setTotalTaches((int) totalCount);
        stat.setTachesTerminees((int) completedCount);
        
        return statistiqueRepository.save(stat);
    }
    
    public List<Statistique> getUserStats(User user) {
        return statistiqueRepository.findByUser(user);
    }
}
