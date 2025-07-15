package com.bibliotheque.gestion_pret.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "exemplaires")
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livre_id", nullable = false)
    @JsonBackReference
    private Livre livre;

    @Column(unique = true, nullable = false)
    private String codeExemplaire;

    // L'état de l'exemplaire (ex: 'NEUF', 'BON', 'ABIME', 'PERDU')
    private String etat;

    private LocalDate dateAchat;

    private boolean disponible = true;
}