package com.bibliotheque.gestion_pret.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class AdherentDetailDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private String typeAbonnement;
    private LocalDate dateAbonnementDebut;
    private LocalDate dateFinAbonnement;
    private String statutPaiementAbonnement;

    private int quotaMaxPrets;
    private int pretsEnCours;
    private int quotaRestant;

    private boolean estSuspendu;
    private LocalDate dateFinSuspension;
    private boolean aDesPenalitesFinancieres;

    private List<PenaliteDto> historiquePenalites;
}