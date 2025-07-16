package com.bibliotheque.gestion_pret.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutProlongation;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.Prolongation;
import com.bibliotheque.gestion_pret.model.TypeAdherent;
import com.bibliotheque.gestion_pret.repository.PretRepository;
import com.bibliotheque.gestion_pret.repository.ProlongationRepository;
import com.bibliotheque.gestion_pret.repository.ReservationRepository;

@Service
public class ProlongationService {

        @Autowired
        private ProlongationRepository prolongationRepository;
        @Autowired
        private PretRepository pretRepository;

        @Autowired
        private ReservationRepository reservationRepository;

        @Transactional
        public void demanderProlongation(Long pretId, Adherent demandeur, String motif) throws Exception {
                // 1. Récupération des informations (inchangé)
                Pret pret = pretRepository.findById(pretId)
                                .orElseThrow(() -> new Exception("Prêt non trouvé."));

                // 2. Vérifications de sécurité et de contexte (inchangé)
                if (!pret.getAdherent().getId().equals(demandeur.getId())) {
                        throw new SecurityException(
                                        "Vous n'êtes pas autorisé à demander une prolongation pour ce prêt.");
                }
                if (pret.getDateRetourPrevue().isBefore(LocalDate.now())) {
                        throw new Exception("Impossible de prolonger un prêt qui est déjà en retard.");
                }
                if (reservationRepository.existsByLivreAndStatut(pret.getLivre(),
                                com.bibliotheque.gestion_pret.enums.StatutReservation.active)) {
                        throw new Exception(
                                        "Impossible de prolonger : ce livre est déjà réservé par un autre adhérent.");
                }
                if (prolongationRepository.existsByPretAndStatut(pret, StatutProlongation.en_attente)) {
                        throw new Exception(
                                        "Vous avez déjà une demande de prolongation en attente de validation pour ce prêt.");
                }

                TypeAdherent typeAdherent = demandeur.getTypeAdherent();

                int maxProlongations = typeAdherent.getDureeProlongationJours();

                if (pret.getNombreProlongations() >= maxProlongations) {
                        throw new Exception("Le nombre maximum de prolongations (" + maxProlongations
                                        + ") a déjà été atteint pour ce prêt.");
                }

                int dureeProlongationEnJours = typeAdherent.getDureePretJours();

                Prolongation demande = new Prolongation();
                demande.setPret(pret);
                demande.setAncienneDateRetour(pret.getDateRetourPrevue());
                demande.setNouvelleDateRetour(pret.getDateRetourPrevue().plusDays(dureeProlongationEnJours));
                demande.setMotif(motif);
                demande.setDemandePar(demandeur);
                demande.setDateDemande(LocalDate.now());
                demande.setStatut(StatutProlongation.en_attente);

                prolongationRepository.save(demande);
        }
}