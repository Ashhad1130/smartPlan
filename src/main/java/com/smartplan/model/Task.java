package com.smartplan.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Category category;
    
    @Column(nullable = false, length = 200)
    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Priorite priorite;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.A_FAIRE;
    
    @Column(name = "date_limite", nullable = false)
    private LocalDate dateLimite;
    
    @Column(name = "score_priorite")
    private Integer scorePriorite = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum Priorite { HAUTE, MOYENNE, BASSE }
    public enum Statut { A_FAIRE, EN_COURS, TERMINE }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Priorite getPriorite() { return priorite; }
    public void setPriorite(Priorite priorite) { this.priorite = priorite; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public LocalDate getDateLimite() { return dateLimite; }
    public void setDateLimite(LocalDate dateLimite) { this.dateLimite = dateLimite; }
    public Integer getScorePriorite() { return scorePriorite; }
    public void setScorePriorite(Integer scorePriorite) { this.scorePriorite = scorePriorite; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
