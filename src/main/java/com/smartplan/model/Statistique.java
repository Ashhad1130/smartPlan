package com.smartplan.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "statistiques")
public class Statistique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "semaine", nullable = false)
    private LocalDate semaine;
    
    @Column(name = "taux_completion", nullable = false)
    private BigDecimal tauxCompletion;
    
    @Column(name = "total_taches", nullable = false)
    private Integer totalTaches;
    
    @Column(name = "taches_terminees", nullable = false)
    private Integer tachesTerminees;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getSemaine() { return semaine; }
    public void setSemaine(LocalDate semaine) { this.semaine = semaine; }
    public BigDecimal getTauxCompletion() { return tauxCompletion; }
    public void setTauxCompletion(BigDecimal tauxCompletion) { this.tauxCompletion = tauxCompletion; }
    public Integer getTotalTaches() { return totalTaches; }
    public void setTotalTaches(Integer totalTaches) { this.totalTaches = totalTaches; }
    public Integer getTachesTerminees() { return tachesTerminees; }
    public void setTachesTerminees(Integer tachesTerminees) { this.tachesTerminees = tachesTerminees; }
}
