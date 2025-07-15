package com.bibliotheque.gestion_pret.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ExemplaireDto {
    private Long id;
    private String codeExemplaire;
    private String etat;
    private LocalDate dateAchat;
    private boolean disponible;
}