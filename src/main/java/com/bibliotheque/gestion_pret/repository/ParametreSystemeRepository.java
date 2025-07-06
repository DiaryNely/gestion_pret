package com.bibliotheque.gestion_pret.repository;

import com.bibliotheque.gestion_pret.model.ParametreSysteme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParametreSystemeRepository extends JpaRepository<ParametreSysteme, Long> {
    Optional<ParametreSysteme> findByNom(String nom);
}