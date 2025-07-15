package com.bibliotheque.gestion_pret.model;

import java.time.LocalDateTime;

import com.bibliotheque.gestion_pret.enums.TypeValeurParametre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "parametres_systeme")
public class ParametreSysteme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String nom;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String valeur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_valeur", length = 20)
    private TypeValeurParametre typeValeur;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean modifiable;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ParametreSysteme(String nom, String valeur) {
        this.nom = nom;
        this.valeur = valeur;
    }
}
