package com.bibliotheque.gestion_pret.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.model.Exemplaire;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.ExemplaireRepository;
import com.bibliotheque.gestion_pret.repository.LivreRepository;

/**
 * Ce script s'exécute une seule fois au démarrage pour peupler la table
 * 'exemplaires' avec des données de test précises, en se basant sur les IDs des
 * livres.
 * 
 * ATTENTION : Après une exécution réussie, il faut commenter ou supprimer
 * l'annotation @Component pour ne pas qu'il se relance.
 */
@Component // <-- DÉCOMMENTEZ CETTE LIGNE POUR EXÉCUTER LE SCRIPT UNE FOIS
public class DataMigrationRunner implements CommandLineRunner {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("======================================================");
        System.out.println("  DÉBUT DU SCRIPT DE CRÉATION MANUELLE DES EXEMPLAIRES (PAR ID)");
        System.out.println("======================================================");

        // On vide la table des exemplaires pour garantir un état propre
        exemplaireRepository.deleteAllInBatch(); // Plus efficace que deleteAll()
        System.out.println("Anciens exemplaires supprimés.");

        // On définit nos données de test en utilisant les IDs des livres.
        // La clé de la Map est l'ID du livre (Long), la valeur est la liste de ses
        // codes d'exemplaires.
        Map<Long, List<String>> donneesExemplaires = Map.of(
                1L, List.of("MIS001", "MIS002", "MIS003"), // Les Misérables
                2L, List.of("ETR001", "ETR002"), // L'Étranger
                3L, List.of("HAR001") // Harry Potter
        );

        int totalExemplairesCrees = 0;

        // On boucle sur nos données de test (sur les IDs)
        for (Map.Entry<Long, List<String>> entry : donneesExemplaires.entrySet()) {
            Long livreId = entry.getKey();
            List<String> codesExemplaires = entry.getValue();

            // On cherche le livre par son ID. C'est plus fiable.
            Optional<Livre> livreOpt = livreRepository.findById(livreId);

            if (livreOpt.isPresent()) {
                Livre livre = livreOpt.get();
                System.out.println("Traitement du livre : '" + livre.getTitre() + "' (ID: " + livre.getId() + ")");

                List<Exemplaire> nouveauxExemplaires = new ArrayList<>();
                for (String code : codesExemplaires) {
                    Exemplaire nouvelExemplaire = new Exemplaire();
                    nouvelExemplaire.setLivre(livre);
                    nouvelExemplaire.setCodeExemplaire(code);
                    nouvelExemplaire.setEtat("BON");
                    nouvelExemplaire.setDateAchat(LocalDate.now());
                    nouvelExemplaire.setDisponible(true);

                    nouveauxExemplaires.add(nouvelExemplaire);
                    totalExemplairesCrees++;
                }

                // On assigne la nouvelle liste au livre
                livre.setExemplaires(nouveauxExemplaires);
                livreRepository.save(livre); // Sauvegarde le livre et cascade la sauvegarde des exemplaires

            } else {
                System.out.println("ATTENTION : Le livre avec l'ID " + livreId + " n'a pas été trouvé. Il est ignoré.");
            }
        }

        System.out.println("======================================================");
        System.out.println("  MIGRATION TERMINÉE : " + totalExemplairesCrees + " exemplaires ont été créés.");
        System.out.println("======================================================");
    }
}