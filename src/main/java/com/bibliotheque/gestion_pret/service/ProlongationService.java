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

@Service
public class ProlongationService {

    @Autowired
    private ProlongationRepository prolongationRepository;
    @Autowired
    private PretRepository pretRepository;
    @Autowired
    private ParametreSystemeRepository parametreSystemeRepository;

    @Transactional
    public void demanderProlongation(Long pretId, Adherent demandeur, String motif) throws Exception {
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new Exception("Prêt non trouvé."));
        ParametreSysteme maxProlongationsParam = parametreSystemeRepository.findByNom("max_prolongations")
                .orElse(new ParametreSysteme("max_prolongations", "1"));
        long maxProlongations = Long.parseLong(maxProlongationsParam.getValeur());

        long prolongationsDejaApprouvees = prolongationRepository.countByPret_IdAndStatut(pretId,
                StatutProlongation.approuvee);

        if (prolongationsDejaApprouvees >= maxProlongations) {
            throw new Exception(
                    "Le nombre maximum de prolongations (" + maxProlongations + ") a déjà été atteint pour ce prêt.");
        }

        if (pret.getDateRetourPrevue().isBefore(LocalDate.now())) {
            throw new Exception("Impossible de prolonger un prêt qui est déjà en retard.");
        }

        ParametreSysteme dureeProlongationParam = parametreSystemeRepository.findByNom("duree_prolongation")
                .orElse(new ParametreSysteme("duree_prolongation", "7"));
        int dureeProlongationJours = Integer.parseInt(dureeProlongationParam.getValeur());

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