package com.bibliotheque.gestion_pret.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutPaiementAdherent;
import com.bibliotheque.gestion_pret.enums.StatutReservation;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.Reservation;
import com.bibliotheque.gestion_pret.model.StatutPret;
import com.bibliotheque.gestion_pret.model.TypePret;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.repository.LivreRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;
import com.bibliotheque.gestion_pret.repository.ReservationRepository;
import com.bibliotheque.gestion_pret.repository.StatutPretRepository;
import com.bibliotheque.gestion_pret.repository.TypePretRepository;

@Service
public class PretService {

        @Autowired
        private PretRepository pretRepository;
        @Autowired
        private LivreRepository livreRepository;
        @Autowired
        private AdherentRepository adherentRepository;
        @Autowired
        private StatutPretRepository statutPretRepository;
        @Autowired
        private TypePretRepository typePretRepository;
        @Autowired
        private ReservationService reservationService;

        @Autowired
        private ReservationRepository reservationRepository;

        @Autowired
        private PenaliteService penaliteService;

        @Autowired
        private ParametreService parametreService;

        @Autowired
        private CalendrierService calendrierService;

        @Transactional
        public void emprunterLivre(Long adherentId, Long livreId, Long typePretId, LocalDate dateEmprunt)
                        throws Exception {
                Livre livre = livreRepository.findById(livreId)
                                .orElseThrow(() -> new Exception("Livre non trouvé avec l'ID : " + livreId));
                Adherent adherent = adherentRepository.findById(adherentId)
                                .orElseThrow(() -> new Exception("Adhérent non trouvé avec l'ID : " + adherentId));

                if (adherent.getAbonnementFin() == null || adherent.getAbonnementFin().isBefore(dateEmprunt)) {
                        throw new Exception("Votre abonnement n'était pas actif à la date d'emprunt sélectionnée.");
                }

                if (!calendrierService.isJourOuvert(dateEmprunt)) {
                        LocalDate prochainJour = calendrierService.getProchainJourOuvert(dateEmprunt);
                        throw new Exception("Emprunt impossible le "
                                        + dateEmprunt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        + " car la bibliothèque est fermée. Prochain jour d'ouverture : "
                                        + prochainJour.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
                }

                if (adherent.getDateFinSuspension() != null
                                && adherent.getDateFinSuspension().isAfter(LocalDate.now())) {
                        throw new Exception("Emprunt impossible : vous êtes suspendu de prêt jusqu'au "
                                        + adherent.getDateFinSuspension()
                                                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        + ".");
                }

                if (adherent.getStatutPaiement() != StatutPaiementAdherent.paye) {
                        throw new Exception("Votre paiement d'abonnement n'est pas à jour.");
                }

                if (penaliteService.aDesPenalitesImpayees(adherentId)) {
                        throw new Exception(
                                        "Emprunt impossible : vous avez des pénalités impayées. Veuillez les régler auprès d'un administrateur.");
                }

                if (livre.getNombreExemplaires() <= 0) {
                        throw new Exception("Désolé, tous les exemplaires de ce livre sont actuellement empruntés.");
                }

                long pretsEnCours = pretRepository.countByAdherent_IdAndStatutPret_Nom(adherentId, "En cours");
                if (pretsEnCours >= adherent.getTypeAdherent().getMaxLivresEmprunt()) {
                        throw new Exception("Vous avez atteint votre limite de "
                                        + adherent.getTypeAdherent().getMaxLivresEmprunt() + " prêts simultanés.");
                }

                if (livre.getRestrictionTypeAdherent() != null && !livre.getRestrictionTypeAdherent().getId()
                                .equals(adherent.getTypeAdherent().getId())) {
                        throw new Exception("Ce livre est réservé à une autre catégorie d'adhérents ("
                                        + livre.getRestrictionTypeAdherent().getNom() + ").");
                }

                Pret nouveauPret = new Pret();
                nouveauPret.setAdherent(adherent);
                nouveauPret.setLivre(livre);
                nouveauPret.setDateEmprunt(dateEmprunt);

                int dureePret = adherent.getTypeAdherent().getDureePretJours();
                nouveauPret.setDateRetourPrevue(dateEmprunt.plusDays(dureePret));

                StatutPret statutEnCours = statutPretRepository.findByNom("En cours")
                                .orElseThrow(() -> new Exception("Statut de prêt 'En cours' non trouvé."));
                TypePret typeChoisi = typePretRepository.findById(typePretId)
                                .orElseThrow(() -> new Exception("Type de prêt invalide sélectionné."));

                nouveauPret.setStatutPret(statutEnCours);
                nouveauPret.setTypePret(typeChoisi);

                pretRepository.save(nouveauPret);
        }

        @Transactional
        public void rendreLivre(Long pretId, LocalDate dateRetourReelle) throws Exception {
                Pret pret = pretRepository.findById(pretId)
                                .orElseThrow(() -> new Exception("Prêt non trouvé avec l'ID : " + pretId));

                if (pret.getDateRetourReelle() != null) {
                        throw new Exception("Ce livre a déjà été marqué comme retourné.");
                }

                if (!calendrierService.isJourOuvert(dateRetourReelle)) {
                        LocalDate prochainJour = calendrierService.getProchainJourOuvert(dateRetourReelle);
                        throw new Exception("Retour impossible le "
                                        + dateRetourReelle.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                        + " car la bibliothèque est fermée. Veuillez enregistrer le retour pour le "
                                        + prochainJour.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
                }

                if (dateRetourReelle.isBefore(pret.getDateEmprunt())) {
                        throw new Exception("La date de retour ne peut pas être antérieure à la date d'emprunt.");
                }

                pret.setDateRetourReelle(dateRetourReelle);
                final long ID_STATUT_RETOURNE = 2;
                StatutPret statutRetourne = statutPretRepository.findById(ID_STATUT_RETOURNE)
                                .orElseThrow(() -> new Exception(
                                                "Statut 'Retourné' non trouvé dans la configuration."));
                pret.setStatutPret(statutRetourne);

                pretRepository.save(pret);
                Livre livreRetourne = pret.getLivre();

                reservationService.traiterRetourLivre(livreRetourne);

                penaliteService.creerPenaliteSiNecessaire(pret);
                calculerEtAppliquerSuspension(pret);

        }

        @Transactional
        public void emprunterLivreReserve(Long reservationId, Long adherentId, Long typePretId, LocalDate dateEmprunt)
                        throws Exception {
                Reservation reservation = reservationRepository.findById(reservationId)
                                .orElseThrow(() -> new IllegalStateException("Réservation non trouvée."));

                if (!reservation.getAdherent().getId().equals(adherentId)) {
                        throw new SecurityException("Cette réservation ne vous appartient pas.");
                }
                if (reservation.getStatut() != StatutReservation.active) {
                        throw new IllegalStateException("Cette réservation n'est pas active.");
                }
                if (reservation.getDateExpiration() == null) {
                        throw new IllegalStateException("Le livre n'est pas encore disponible pour vous.");
                }
                if (reservation.getDateExpiration().isBefore(dateEmprunt)) {
                        reservation.setStatut(StatutReservation.annulee);
                        reservation.setNotes("La réservation a expiré le " + reservation.getDateExpiration());
                        reservationRepository.save(reservation);
                        reservationService.traiterRetourLivre(reservation.getLivre());
                        throw new IllegalStateException(
                                        "La date limite pour récupérer ce livre est dépassée. La réservation a été annulée.");
                }

                reservation.setStatut(StatutReservation.terminee);
                reservation.setNotes("Livre emprunté le " + dateEmprunt);
                reservationRepository.save(reservation);

                Long livreId = reservation.getLivre().getId();
                emprunterLivre(adherentId, livreId, typePretId, dateEmprunt);
        }

        private void calculerEtAppliquerSuspension(Pret pret) {
                LocalDate dateRetourPrevue = pret.getDateRetourPrevue();
                LocalDate dateRetourReelle = pret.getDateRetourReelle();

                int joursTolerance = parametreService.getJoursTolerance();
                if (dateRetourReelle == null || !dateRetourReelle.isAfter(dateRetourPrevue.plusDays(joursTolerance))) {
                        return;
                }

                long joursDeRetard = ChronoUnit.DAYS.between(dateRetourPrevue.plusDays(joursTolerance),
                                dateRetourReelle);
                int ratioSuspension = parametreService.getRatioSuspensionParJourRetard();

                if (ratioSuspension <= 0) {
                        return;
                }

                long joursDeSuspension = joursDeRetard * ratioSuspension;

                Adherent adherent = pret.getAdherent();
                LocalDate dateDebutSuspension = LocalDate.now();
                if (adherent.getDateFinSuspension() != null
                                && adherent.getDateFinSuspension().isAfter(dateDebutSuspension)) {
                        dateDebutSuspension = adherent.getDateFinSuspension().plusDays(1);
                }

                LocalDate nouvelleDateFinSuspension = dateDebutSuspension.plusDays(joursDeSuspension);
                adherent.setDateFinSuspension(nouvelleDateFinSuspension);
                adherentRepository.save(adherent);
        }
}