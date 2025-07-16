package com.bibliotheque.gestion_pret.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bibliotheque.gestion_pret.enums.StatutProlongation;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.Prolongation;

public interface ProlongationRepository extends JpaRepository<Prolongation, Long> {
    long countByPret_IdAndStatut(Long pretId, StatutProlongation statut);

    List<Prolongation> findByStatut(StatutProlongation statut);

    public boolean existsByPretAndStatut(Pret pret, StatutProlongation statutProlongation);
}