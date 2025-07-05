package com.bibliotheque.gestion_pret.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.model.TypeAdherent;

@Repository
public interface TypeAdherentRepository extends JpaRepository<TypeAdherent, Long> {
}