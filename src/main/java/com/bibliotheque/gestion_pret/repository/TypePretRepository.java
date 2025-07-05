package com.bibliotheque.gestion_pret.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bibliotheque.gestion_pret.model.TypePret;

public interface TypePretRepository extends JpaRepository<TypePret, Long> {
    Optional<TypePret> findByNom(String nom);
}
