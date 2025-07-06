package com.bibliotheque.gestion_pret.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Import
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutProlongation;
import com.bibliotheque.gestion_pret.model.Adherent; // Import
import com.bibliotheque.gestion_pret.model.ParametreSysteme;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.Prolongation;
import com.bibliotheque.gestion_pret.repository.ParametreSystemeRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;
import com.bibliotheque.gestion_pret.repository.ProlongationRepository;

@Service
public class ProlongationService {

    @Autowired
    private ProlongationRepository prolongationRepository;
    @Autowired
    private PretRepository pretRepository;
    // On réactive cette injection maintenant que le repository existe
    @Autowired
    private ParametreSystemeRepository parametreSystemeRepository;

    @Transactional
    public void demanderProlongation(Long pretId, Adherent demandeur, String motif) throws Exception {
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new Exception("Prêt non trouvé."));

        // ===== ON REVIENT À LA LECTURE DYNAMIQUE DEPUIS LA BDD =====

        // Règle : vérifier le nombre max de prolongations
        // .orElse() est une sécurité : si le paramètre n'existe pas en BDD, on utilise
        // une valeur par défaut.
        ParametreSysteme maxProlongationsParam = parametreSystemeRepository.findByNom("max_prolongations")
                .orElse(new ParametreSysteme("max_prolongations", "1")); // Valeur par défaut : 1 seule prolongation
        long maxProlongations = Long.parseLong(maxProlongationsParam.getValeur());

        long prolongationsDejaApprouvees = prolongationRepository.countByPret_IdAndStatut(pretId, "approuvee");

        if (prolongationsDejaApprouvees >= maxProlongations) {
            throw new Exception(
                    "Le nombre maximum de prolongations (" + maxProlongations + ") a déjà été atteint pour ce prêt.");
        }

        // Règle : On ne peut pas prolonger un prêt déjà en retard (inchangé)
        if (pret.getDateRetourPrevue().isBefore(LocalDate.now())) {
            throw new Exception("Impossible de prolonger un prêt qui est déjà en retard.");
        }

        // Récupérer la durée de prolongation depuis les paramètres système
        ParametreSysteme dureeProlongationParam = parametreSystemeRepository.findByNom("duree_prolongation")
                .orElse(new ParametreSysteme("duree_prolongation", "7")); // 7 jours par défaut
        int dureeProlongationJours = Integer.parseInt(dureeProlongationParam.getValeur());

        // Créer la demande de prolongation (inchangé)
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