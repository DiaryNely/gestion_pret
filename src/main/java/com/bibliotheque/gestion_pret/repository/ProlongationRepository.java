package com.bibliotheque.gestion_pret.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bibliotheque.gestion_pret.enums.StatutProlongation;
import com.bibliotheque.gestion_pret.model.Prolongation;

public interface ProlongationRepository extends JpaRepository<Prolongation, Long> {
    // Compte combien de prolongations (approuvées) existent déjà pour un prêt
    long countByPret_IdAndStatut(Long pretId, String statut);

    // Trouve toutes les demandes avec un certain statut
    List<Prolongation> findByStatut(StatutProlongation statut);
}