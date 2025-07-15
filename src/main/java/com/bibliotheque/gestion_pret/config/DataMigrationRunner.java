package com.bibliotheque.gestion_pret.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.model.Exemplaire;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.LivreRepository;

@Component
public class DataMigrationRunner implements CommandLineRunner {

    @Autowired
    private LivreRepository livreRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("======================================================");
        System.out.println("  DÉBUT DU SCRIPT DE MIGRATION DES EXEMPLAIRES");
        System.out.println("======================================================");

        List<Livre> tousLesLivres = livreRepository.findAll();
        int livresMigres = 0;

        for (Livre livre : tousLesLivres) {
            if ((livre.getExemplaires() == null || livre.getExemplaires().isEmpty())
                    && livre.getNombreExemplaires() != null && livre.getNombreExemplaires() > 0) {

                System.out.println("Migration du livre : '" + livre.getTitre() + "' (ID: " + livre.getId() + ") - "
                        + livre.getNombreExemplaires() + " exemplaires à créer.");

                List<Exemplaire> nouveauxExemplaires = new ArrayList<>();
                for (int i = 1; i <= livre.getNombreExemplaires(); i++) {
                    Exemplaire nouvelExemplaire = new Exemplaire();
                    nouvelExemplaire.setLivre(livre);
                    nouvelExemplaire.setCodeExemplaire("L" + livre.getId() + "-E" + i);
                    nouvelExemplaire.setEtat("BON");
                    nouvelExemplaire.setDateAchat(LocalDate.now());
                    nouvelExemplaire.setDisponible(true);

                    nouveauxExemplaires.add(nouvelExemplaire);
                }

                livre.setExemplaires(nouveauxExemplaires);

                livreRepository.save(livre);
                livresMigres++;
            }
        }

        System.out.println("======================================================");
        if (livresMigres > 0) {
            System.out.println("  MIGRATION TERMINÉE : " + livresMigres + " livres ont été mis à jour.");
        } else {
            System.out.println("  AUCUNE MIGRATION NÉCESSAIRE : Tous les livres ont déjà des exemplaires.");
        }
        System.out.println("======================================================");
    }
}