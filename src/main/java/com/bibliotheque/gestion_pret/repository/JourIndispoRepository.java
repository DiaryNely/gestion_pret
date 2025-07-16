package com.bibliotheque.gestion_pret.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.model.JourIndispo;

@Repository
public interface JourIndispoRepository extends JpaRepository<JourIndispo, Long> {

    boolean existsByDateIndispo(LocalDate date);

    @Query("SELECT ji.dateIndispo FROM JourIndispo ji")
    Set<LocalDate> findAllDatesIndispo();
}