package com.bibliotheque.gestion_pret.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.enums.StatutReservation;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByAdherentIdOrderByDateReservationDesc(Long adherentId);

    boolean existsByLivreAndAdherentAndStatut(Livre livre, Adherent adherent, StatutReservation statut);

    long countByLivreAndStatut(Livre livre, StatutReservation statut);

    Optional<Reservation> findFirstByLivreAndStatutOrderByDateReservationAsc(Livre livre, StatutReservation statut);

    List<Reservation> findByLivreAndStatutOrderByDateReservationAsc(Livre livre, StatutReservation statut);

    long countByAdherentIdAndStatut(Long adherentId, StatutReservation active);

    boolean existsByLivreAndStatut(Livre livre, StatutReservation active);

}