package com.bibliotheque.gestion_pret.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutReservation;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Reservation;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.repository.LivreRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;
import com.bibliotheque.gestion_pret.repository.ReservationRepository;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private LivreRepository livreRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private PretRepository pretRepository;

    public Reservation creerReservation(Long adherentId, Long livreId) {
        Livre livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new IllegalStateException("Le livre avec l'ID " + livreId + " n'existe pas."));
        Adherent adherent = adherentRepository.findById(adherentId)
                .orElseThrow(() -> new IllegalStateException("L'adhérent avec l'ID " + adherentId + " n'existe pas."));

        int exemplairesDisponibles = new LivreService().calculerNbExemplairesDisponiblesPourService(livre,
                pretRepository);
        if (exemplairesDisponibles > 0) {
            throw new IllegalStateException("Impossible de réserver : ce livre est disponible pour l'emprunt.");
        }

        if (pretRepository.existsByLivreAndAdherentAndDateRetourReelleIsNull(livre, adherent)) {
            throw new IllegalStateException("Vous avez déjà un prêt en cours pour ce livre.");
        }

        if (reservationRepository.existsByLivreAndAdherentAndStatut(livre, adherent, StatutReservation.active)) {
            throw new IllegalStateException("Vous avez déjà une réservation active pour ce livre.");
        }

        long reservationsActives = reservationRepository.countByAdherentIdAndStatut(adherentId,
                StatutReservation.active);

        // Vérification du quota de réservations
        if (reservationsActives >= adherent.getTypeAdherent().getMaxReservationsActives()) {
            throw new IllegalStateException(
                    "Limite de " + adherent.getTypeAdherent().getMaxReservationsActives() + " réservations atteinte.");
        }

        Reservation reservation = new Reservation();
        reservation.setAdherent(adherent);
        reservation.setLivre(livre);
        reservation.setDateReservation(LocalDate.now());
        reservation.setStatut(StatutReservation.active);
        reservation.setCreatedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    private static class LivreService {
        int calculerNbExemplairesDisponiblesPourService(Livre livre, PretRepository pretRepo) {
            if (livre == null || livre.getId() == null || livre.getNombreExemplaires() == null)
                return 0;
            int total = livre.getNombreExemplaires();
            long enPret = pretRepo.countByLivreIdAndDateRetourReelleIsNull(livre.getId());
            return Math.max(0, total - (int) enPret);
        }
    }

    public void traiterRetourLivre(Livre livre) {

        Optional<Reservation> prochaineReservationOpt = reservationRepository
                .findFirstByLivreAndStatutOrderByDateReservationAsc(livre, StatutReservation.active);

        if (prochaineReservationOpt.isPresent()) {
            Reservation reservationANotifier = prochaineReservationOpt.get();

            LocalDate dateExpiration = LocalDate.now().plusDays(3);
            reservationANotifier.setDateExpiration(dateExpiration);

            reservationANotifier.setNotes("Livre devenu disponible le " + LocalDate.now()
                    + ". À récupérer avant le " + dateExpiration);

            reservationRepository.save(reservationANotifier);

            System.out.println("Notification : Le livre '" + livre.getTitre()
                    + "' est maintenant disponible pour l'adhérent ID " + reservationANotifier.getAdherent().getId()
                    + ". Date limite : " + dateExpiration);

        } else {
            System.out.println("Retour du livre '" + livre.getTitre() + "'. Personne en file d'attente.");
        }
    }

    public List<Reservation> listerReservationsParAdherent(Long adherentId) {
        return reservationRepository.findByAdherentIdOrderByDateReservationDesc(adherentId);
    }

    public void annulerReservation(Long reservationId, Long adherentId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("Réservation non trouvée."));

        if (!reservation.getAdherent().getId().equals(adherentId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à annuler cette réservation.");
        }

        if (reservation.getStatut() != StatutReservation.active) {
            throw new IllegalStateException("Cette réservation ne peut plus être annulée.");
        }

        reservation.setStatut(StatutReservation.annulee);
        reservation.setNotes("Annulée par l'utilisateur le " + LocalDate.now());
        reservationRepository.save(reservation);

        if (reservation.getDateExpiration() != null) {
            traiterRetourLivre(reservation.getLivre());
        }
    }
}