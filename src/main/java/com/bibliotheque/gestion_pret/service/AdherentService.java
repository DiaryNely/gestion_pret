package com.bibliotheque.gestion_pret.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;

@Service
public class AdherentService {

    @Autowired
    private AdherentRepository adherentRepository;

    public void leverSuspension(Long adherentId) {
        Adherent adherent = adherentRepository.findById(adherentId)
                .orElseThrow(() -> new IllegalStateException("Adhérent non trouvé avec l'ID : " + adherentId));

        adherent.setDateFinSuspension(null);

        adherentRepository.save(adherent);
    }

}