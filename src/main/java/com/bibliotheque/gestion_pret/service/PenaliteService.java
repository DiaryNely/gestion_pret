package com.bibliotheque.gestion_pret.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutPaiementPenalite;
import com.bibliotheque.gestion_pret.model.Penalite;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.repository.PenaliteRepository;

@Service
@Transactional
public class PenaliteService {

    @Autowired
    private PenaliteRepository penaliteRepository;

    private static final BigDecimal COUT_PAR_JOUR_DE_RETARD = new BigDecimal("0.20");

    public void creerPenaliteSiNecessaire(Pret pret) {
        LocalDate dateRetourPrevue = pret.getDateRetourPrevue();
        LocalDate dateRetourReelle = pret.getDateRetourReelle();

        if (dateRetourReelle == null || !dateRetourReelle.isAfter(dateRetourPrevue)) {
            return;
        }

        long joursDeRetard = ChronoUnit.DAYS.between(dateRetourPrevue, dateRetourReelle);

        BigDecimal montant = COUT_PAR_JOUR_DE_RETARD.multiply(new BigDecimal(joursDeRetard));

        Penalite nouvellePenalite = new Penalite();
        nouvellePenalite.setPret(pret);
        nouvellePenalite.setAdherent(pret.getAdherent());
        nouvellePenalite.setMontant(montant);
        nouvellePenalite.setJoursRetard((int) joursDeRetard);
        nouvellePenalite.setDateCalcul(LocalDate.now());
        nouvellePenalite.setStatutPaiement(StatutPaiementPenalite.impaye);
        nouvellePenalite.setCreatedAt(LocalDateTime.now());

        penaliteRepository.save(nouvellePenalite);

        System.out.println("Pénalité de " + montant + "€ créée pour l'adhérent ID "
                + pret.getAdherent().getId() + " pour " + joursDeRetard + " jours de retard.");
    }

    public boolean aDesPenalitesImpayees(Long adherentId) {
        return penaliteRepository.existsByAdherentIdAndStatutPaiement(adherentId, StatutPaiementPenalite.impaye);
    }

    public List<Penalite> listerPenalitesParAdherent(Long adherentId) {
        return penaliteRepository.findByAdherentIdOrderByDateCalculDesc(adherentId);
    }

    public List<Penalite> listerPenalitesParStatut(StatutPaiementPenalite statut) {
        return penaliteRepository.findByStatutPaiement(statut);
    }

    public Penalite changerStatutPenalite(Long penaliteId, StatutPaiementPenalite nouveauStatut, String notes) {
        Penalite penalite = penaliteRepository.findById(penaliteId)
                .orElseThrow(() -> new IllegalStateException("Pénalité non trouvée avec l'ID : " + penaliteId));

        penalite.setStatutPaiement(nouveauStatut);

        if (nouveauStatut == StatutPaiementPenalite.paye) {
            penalite.setDatePaiement(LocalDate.now());
        } else {
            penalite.setDatePaiement(null);
        }

        penalite.setNotes(notes);

        return penaliteRepository.save(penalite);
    }
}