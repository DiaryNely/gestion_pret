package com.bibliotheque.gestion_pret.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutProlongation;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.ParametreSysteme;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.Prolongation;
import com.bibliotheque.gestion_pret.repository.ParametreSystemeRepository;
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
        private ParametreSystemeRepository parametreSystemeRepository;

        @Autowired
        private ReservationRepository reservationRepository;

        @Transactional
        public void demanderProlongation(Long pretId, Adherent demandeur, String motif) throws Exception {
                Pret pret = pretRepository.findById(pretId)
                                .orElseThrow(() -> new Exception("Prêt non trouvé."));

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

                ParametreSysteme maxProlongationsParam = parametreSystemeRepository.findByNom("max_prolongations")
                                .orElse(new ParametreSysteme("max_prolongations", "1")); // Vous devriez utiliser
                                                                                         // ParametreService ici, mais
                                                                                         // gardons votre code pour
                                                                                         // l'instant.
                long maxProlongations = Long.parseLong(maxProlongationsParam.getValeur());
                long prolongationsDejaApprouvees = prolongationRepository.countByPret_IdAndStatut(pretId,
                                StatutProlongation.approuvee);
                if (prolongationsDejaApprouvees >= maxProlongations) {
                        throw new Exception("Le nombre maximum de prolongations (" + maxProlongations
                                        + ") a déjà été atteint pour ce prêt.");
                }

                int dureeProlongationJours = demandeur.getTypeAdherent().getDureeProlongationJours();

                Prolongation demande = new Prolongation();
                demande.setPret(pret);
                demande.setAncienneDateRetour(pret.getDateRetourPrevue());
                demande.setNouvelleDateRetour(pret.getDateRetourPrevue().plusDays(dureeProlongationJours));
                demande.setMotif(motif);
                demande.setDemandePar(demandeur);
                demande.setDateDemande(LocalDate.now());
                demande.setStatut(StatutProlongation.en_attente);

                prolongationRepository.save(demande);
        }
}