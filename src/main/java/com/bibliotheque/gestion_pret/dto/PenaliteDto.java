package com.bibliotheque.gestion_pret.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PenaliteDto {
    private Long id;
    private String livreTitre;
    private BigDecimal montant;
    private int joursRetard;
    private String statutPaiement;
    private LocalDate dateCalcul;
}