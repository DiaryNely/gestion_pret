package com.bibliotheque.gestion_pret.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.model.Livre;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long>, JpaSpecificationExecutor<Livre> {
}
