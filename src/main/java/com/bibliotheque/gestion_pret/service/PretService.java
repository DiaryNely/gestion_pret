package com.bibliotheque.gestion_pret.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.enums.StatutPaiementAdherent;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.model.StatutPret;
import com.bibliotheque.gestion_pret.model.TypePret;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.repository.LivreRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;
import com.bibliotheque.gestion_pret.repository.StatutPretRepository;
import com.bibliotheque.gestion_pret.repository.TypePretRepository;

@Service
public class PretService {

        @Autowired
        private PretRepository pretRepository;
        @Autowired
        private LivreRepository livreRepository;
        @Autowired
        private AdherentRepository adherentRepository;
        @Autowired
        private StatutPretRepository statutPretRepository; // A créer
        @Autowired
        private TypePretRepository typePretRepository; // A créer

        @Transactional // Transactionnel car on modifie plusieurs tables (prets, livres)
        public void emprunterLivre(Long adherentId, Long livreId) throws Exception {
                // 1. Récupérer les objets de la base de données
                Livre livre = livreRepository.findById(livreId)
                                .orElseThrow(() -> new Exception("Livre non trouvé avec l'ID : " + livreId));
                Adherent adherent = adherentRepository.findById(adherentId)
                                .orElseThrow(() -> new Exception("Adhérent non trouvé avec l'ID : " + adherentId));

                // 2. Vérifier les règles de gestion (Cahier des charges)
                // Règle 4.4 : Abonnement valide ?
                if (adherent.getAbonnementFin() == null || adherent.getAbonnementFin().isBefore(LocalDate.now())
                                || adherent.getStatutPaiement() != StatutPaiementAdherent.paye) {
                        throw new Exception("Votre abonnement n'est pas actif ou votre paiement n'est pas à jour.");
                }

                // Règle : Nombre d'exemplaires disponibles ?
                if (livre.getNombreExemplaires() <= 0) {
                        throw new Exception("Désolé, tous les exemplaires de ce livre sont actuellement empruntés.");
                }

                long pretsEnCours = pretRepository.countByAdherent_IdAndStatutPret_Nom(adherentId, "En cours");

                // Règle 4.1 : Nombre maximum de livres empruntés atteint ?
                if (pretsEnCours >= adherent.getTypeAdherent().getMaxLivresEmprunt()) {
                        throw new Exception("Vous avez atteint votre limite de "
                                        + adherent.getTypeAdherent().getMaxLivresEmprunt()
                                        + " prêts simultanés.");
                }

                // Règle 4.2 : Restriction sur le type d'adhérent ?
                if (livre.getRestrictionTypeAdherent() != null
                                && !livre.getRestrictionTypeAdherent().getId()
                                                .equals(adherent.getTypeAdherent().getId())) {
                        throw new Exception("Ce livre est réservé à une autre catégorie d'adhérents ("
                                        + livre.getRestrictionTypeAdherent().getNom() + ").");
                }

                // 3. Toutes les règles sont respectées : on peut créer le prêt
                Pret nouveauPret = new Pret();
                nouveauPret.setAdherent(adherent);
                nouveauPret.setLivre(livre);
                nouveauPret.setDateEmprunt(LocalDate.now());

                // Calcul de la date de retour prévue selon le type d'adhérent
                int dureePret = adherent.getTypeAdherent().getDureePretJours();
                nouveauPret.setDateRetourPrevue(LocalDate.now().plusDays(dureePret));

                // Récupérer les statuts et types par défaut (ici, on suppose qu'ils existent
                // avec ces noms)
                StatutPret statutEnCours = statutPretRepository.findByNom("En cours")
                                .orElseThrow(() -> new Exception("Statut de prêt 'En cours' non trouvé."));
                TypePret typeDomicile = typePretRepository.findByNom("Prêt à domicile")
                                .orElseThrow(() -> new Exception("Type de prêt 'Prêt à domicile' non trouvé."));

                nouveauPret.setStatutPret(statutEnCours);
                nouveauPret.setTypePret(typeDomicile);

                // Sauvegarder le nouveau prêt
                pretRepository.save(nouveauPret);

                // 4. Mettre à jour le nombre d'exemplaires du livre
                livre.setNombreExemplaires(livre.getNombreExemplaires() - 1);
                livreRepository.save(livre);
        }

        @Transactional
        public void rendreLivre(Long pretId, LocalDate dateRetourReelle) throws Exception {
                // 1. Récupérer le prêt
                Pret pret = pretRepository.findById(pretId)
                                .orElseThrow(() -> new Exception("Prêt non trouvé avec l'ID : " + pretId));

                // 2. Vérifier si le livre n'est pas déjà rendu
                if (pret.getDateRetourReelle() != null) {
                        throw new Exception("Ce livre a déjà été marqué comme retourné.");
                }

                // 3. Valider la date
                if (dateRetourReelle.isBefore(pret.getDateEmprunt())) {
                        throw new Exception("La date de retour ne peut pas être antérieure à la date d'emprunt.");
                }

                // 4. Mettre à jour les informations du prêt
                pret.setDateRetourReelle(dateRetourReelle);

                // Mettre à jour le statut du prêt
                StatutPret statutRetourne = statutPretRepository.findByNom("Retourné")
                                .orElseThrow(() -> new Exception(
                                                "Statut 'Retourné' non trouvé dans la configuration."));
                pret.setStatutPret(statutRetourne);

                pretRepository.save(pret);

                // 5. Remettre à jour le stock du livre (incrémenter le nombre d'exemplaires)
                Livre livre = pret.getLivre();
                livre.setNombreExemplaires(livre.getNombreExemplaires() + 1);
                livreRepository.save(livre);
        }
}