package com.bibliotheque.gestion_pret.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutProlongation;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.Prolongation;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.repository.LivreRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;
import com.bibliotheque.gestion_pret.repository.ProlongationRepository;

@Service
@Transactional(readOnly = true)
public class AdminService {

    @Autowired
    private LivreRepository livreRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private PretRepository pretRepository;
    @Autowired
    private ProlongationRepository prolongationRepository;

    public List<Livre> getAllLivres() {
        return livreRepository.findAll();
    }

    public List<Adherent> getAllAdherents() {
        return adherentRepository.findAll();
    }

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalLivres", livreRepository.count());
        stats.put("totalAdherents", adherentRepository.count());

        stats.put("pretsEnCours", pretRepository.countByStatutPret_Nom("En cours"));
        stats.put("pretsEnRetard", pretRepository.countByStatutPret_Nom("En retard"));

        stats.put("derniersPrets", pretRepository.findTop5ByOrderByDateEmpruntDesc());
        stats.put("listePretsEnRetard", pretRepository.findByStatutPret_Nom("En retard"));

        return stats;
    }

    public List<Pret> getAllPrets() {
        return pretRepository.findAll();
    }

    public List<Prolongation> getDemandesEnAttente() {
        return prolongationRepository.findByStatut(StatutProlongation.en_attente);
    }

    @Transactional
    public void traiterDemandeProlongation(Long prolongationId, String action, Long adminId) throws Exception {
        Prolongation demande = prolongationRepository.findById(prolongationId)
                .orElseThrow(() -> new Exception("Demande de prolongation non trouvée."));

        Adherent admin = new Adherent();
        admin.setId(adminId);
        demande.setApprouvePar(admin);
        demande.setDateApprobation(LocalDate.now());

        if (null == action) {
            throw new Exception("Action non valide.");
        } else
            switch (action) {
                case "approuver" -> {
                    demande.setStatut(StatutProlongation.approuvee);
                    Pret pret = demande.getPret();
                    pret.setDateRetourPrevue(demande.getNouvelleDateRetour());
                    pretRepository.save(pret);
                }
                case "refuser" -> demande.setStatut(StatutProlongation.refusee);
                default -> throw new Exception("Action non valide.");
            }

        prolongationRepository.save(demande);
    }
}