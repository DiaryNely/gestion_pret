package com.bibliotheque.gestion_pret.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.PretRepository;

@Service
public class LivreService {
    @Autowired
    private PretRepository pretRepository;

    public int calculerNbExemplairesDisponibles(Livre livre) {
        if (livre == null || livre.getId() == null || livre.getNombreExemplaires() == null) {
            return 0;
        }

        int totalExemplaires = livre.getNombreExemplaires();

        long nbPretsEnCours = pretRepository.countByLivreIdAndDateRetourReelleIsNull(livre.getId());

        int disponibles = totalExemplaires - (int) nbPretsEnCours;

        return Math.max(0, disponibles);
    }
}
