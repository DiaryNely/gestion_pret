package com.bibliotheque.gestion_pret.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bibliotheque.gestion_pret.model.StatutPret;

public interface StatutPretRepository extends JpaRepository<StatutPret, Long> {
    Optional<StatutPret> findByNom(String nom);
}