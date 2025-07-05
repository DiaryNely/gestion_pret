package com.bibliotheque.gestion_pret.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.repository.LivreRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;

@Service
@Transactional(readOnly = true) // Appliqué à toute la classe pour la lecture
public class AdminService {

    @Autowired
    private LivreRepository livreRepository;
    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private PretRepository pretRepository;

    public List<Livre> getAllLivres() {
        return livreRepository.findAll();
    }

    public List<Adherent> getAllAdherents() {
        return adherentRepository.findAll();
    }

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques globales
        stats.put("totalLivres", livreRepository.count());
        stats.put("totalAdherents", adherentRepository.count());

        // Statistiques sur les prêts
        stats.put("pretsEnCours", pretRepository.countByStatutPret_Nom("En cours"));
        stats.put("pretsEnRetard", pretRepository.countByStatutPret_Nom("En retard"));

        // Listes pour les tableaux de bord
        stats.put("derniersPrets", pretRepository.findTop5ByOrderByDateEmpruntDesc());
        stats.put("listePretsEnRetard", pretRepository.findByStatutPret_Nom("En retard"));

        return stats;
    }
}