package com.bibliotheque.gestion_pret.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.model.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
    // Pour l'instant, les méthodes de base de JpaRepository suffisent.
}
