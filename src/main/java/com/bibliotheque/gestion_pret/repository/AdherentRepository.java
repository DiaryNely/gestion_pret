package com.bibliotheque.gestion_pret.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.model.Adherent;

@Repository
public interface AdherentRepository extends JpaRepository<Adherent, Long> {
    @EntityGraph(attributePaths = { "typeAdherent" })
    Optional<Adherent> findByEmail(String email);
}