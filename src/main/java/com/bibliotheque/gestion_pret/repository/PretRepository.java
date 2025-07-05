package com.bibliotheque.gestion_pret.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.model.Pret;

@Repository
public interface PretRepository extends JpaRepository<Pret, Long> {

    // Compte les prêts ayant un certain statut (ex: 'En cours')
    long countByStatutPret_Nom(String statutNom);

    // Trouve les 5 derniers prêts effectués, triés par date d'emprunt
    List<Pret> findTop5ByOrderByDateEmpruntDesc();

    // Trouve tous les prêts ayant un certain statut
    List<Pret> findByStatutPret_Nom(String statutNom);

    @Override
    @EntityGraph(attributePaths = { "livre", "adherent", "statutPret", "typePret" })
    List<Pret> findAll();
}
