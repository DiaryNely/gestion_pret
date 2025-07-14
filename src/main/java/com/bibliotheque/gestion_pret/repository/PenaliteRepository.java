package com.bibliotheque.gestion_pret.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.enums.StatutPaiementPenalite;
import com.bibliotheque.gestion_pret.model.Penalite;

@Repository
public interface PenaliteRepository extends JpaRepository<Penalite, Long> {

    List<Penalite> findByAdherentIdOrderByDateCalculDesc(Long adherentId);

    boolean existsByAdherentIdAndStatutPaiement(Long adherentId, StatutPaiementPenalite statut);

    List<Penalite> findByStatutPaiement(StatutPaiementPenalite statut);
}