package com.bibliotheque.gestion_pret.dto;

import java.util.List;

import lombok.Data;

@Data
public class LivreDto {
    private Long id;
    private String titre;
    private String auteur;
    private String langue;
    private String genre;
    private List<ExemplaireDto> exemplaires;
}