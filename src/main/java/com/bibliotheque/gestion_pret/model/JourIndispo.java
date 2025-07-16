package com.bibliotheque.gestion_pret.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "jour_indispo")
public class JourIndispo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_indispo", nullable = false, unique = true)
    private LocalDate dateIndispo;

    @Column(name = "valeur", length = 100)
    private String valeur;
}