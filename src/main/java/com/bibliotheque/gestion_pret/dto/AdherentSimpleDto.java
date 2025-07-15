package com.bibliotheque.gestion_pret.dto;

import lombok.Data;

@Data
public class AdherentSimpleDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private boolean actif;
}