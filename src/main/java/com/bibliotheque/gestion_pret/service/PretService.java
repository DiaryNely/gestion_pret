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
        private StatutPretRepository statutPretRepository;
        @Autowired
        private TypePretRepository typePretRepository;

        @Transactional
        public void emprunterLivre(Long adherentId, Long livreId, Long typePretId) throws Exception {
                Livre livre = livreRepository.findById(livreId)
                                .orElseThrow(() -> new Exception("Livre non trouvé avec l'ID : " + livreId));
                Adherent adherent = adherentRepository.findById(adherentId)
                                .orElseThrow(() -> new Exception("Adhérent non trouvé avec l'ID : " + adherentId));

                if (adherent.getAbonnementFin() == null || adherent.getAbonnementFin().isBefore(LocalDate.now())
                                || adherent.getStatutPaiement() != StatutPaiementAdherent.paye) {
                        throw new Exception("Votre abonnement n'est pas actif ou votre paiement n'est pas à jour.");
                }

                if (livre.getNombreExemplaires() <= 0) {
                        throw new Exception("Désolé, tous les exemplaires de ce livre sont actuellement empruntés.");
                }

                long pretsEnCours = pretRepository.countByAdherent_IdAndStatutPret_Nom(adherentId, "En cours");

                if (pretsEnCours >= adherent.getTypeAdherent().getMaxLivresEmprunt()) {
                        throw new Exception("Vous avez atteint votre limite de "
                                        + adherent.getTypeAdherent().getMaxLivresEmprunt()
                                        + " prêts simultanés.");
                }

                if (livre.getRestrictionTypeAdherent() != null
                                && !livre.getRestrictionTypeAdherent().getId()
                                                .equals(adherent.getTypeAdherent().getId())) {
                        throw new Exception("Ce livre est réservé à une autre catégorie d'adhérents ("
                                        + livre.getRestrictionTypeAdherent().getNom() + ").");
                }

                Pret nouveauPret = new Pret();
                nouveauPret.setAdherent(adherent);
                nouveauPret.setLivre(livre);
                nouveauPret.setDateEmprunt(LocalDate.now());

                int dureePret = adherent.getTypeAdherent().getDureePretJours();
                nouveauPret.setDateRetourPrevue(LocalDate.now().plusDays(dureePret));

                StatutPret statutEnCours = statutPretRepository.findByNom("En cours")
                                .orElseThrow(() -> new Exception("Statut de prêt 'En cours' non trouvé."));
                TypePret typeChoisi = typePretRepository.findById(typePretId)
                                .orElseThrow(() -> new Exception("Type de prêt invalide sélectionné."));
                nouveauPret.setStatutPret(statutEnCours);
                nouveauPret.setTypePret(typeChoisi);

                pretRepository.save(nouveauPret);

                livre.setNombreExemplaires(livre.getNombreExemplaires() - 1);
                livreRepository.save(livre);
        }

        @Transactional
        public void rendreLivre(Long pretId, LocalDate dateRetourReelle) throws Exception {
                Pret pret = pretRepository.findById(pretId)
                                .orElseThrow(() -> new Exception("Prêt non trouvé avec l'ID : " + pretId));

                if (pret.getDateRetourReelle() != null) {
                        throw new Exception("Ce livre a déjà été marqué comme retourné.");
                }

                if (dateRetourReelle.isBefore(pret.getDateEmprunt())) {
                        throw new Exception("La date de retour ne peut pas être antérieure à la date d'emprunt.");
                }

                pret.setDateRetourReelle(dateRetourReelle);
                final long ID_STATUT_RETOURNE = 2;
                StatutPret statutRetourne = statutPretRepository.findById(ID_STATUT_RETOURNE)
                                .orElseThrow(() -> new Exception(
                                                "Statut 'Retourné' non trouvé dans la configuration."));
                pret.setStatutPret(statutRetourne);

                pretRepository.save(pret);

                Livre livre = pret.getLivre();
                livre.setNombreExemplaires(livre.getNombreExemplaires() + 1);
                livreRepository.save(livre);
        }
}