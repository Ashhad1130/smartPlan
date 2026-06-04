package com.smartplan.repository;

import com.smartplan.model.Statistique;
import com.smartplan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface StatistiqueRepository extends JpaRepository<Statistique, Long> {
    List<Statistique> findByUser(User user);
    Optional<Statistique> findByUserAndSemaine(User user, LocalDate semaine);
}
