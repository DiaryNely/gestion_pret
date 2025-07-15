package com.bibliotheque.gestion_pret.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotheque.gestion_pret.dto.AdherentDetailDto;
import com.bibliotheque.gestion_pret.dto.AdherentSimpleDto;
import com.bibliotheque.gestion_pret.dto.PenaliteDto;
import com.bibliotheque.gestion_pret.enums.StatutPaiementPenalite;
import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Penalite;
import com.bibliotheque.gestion_pret.repository.AdherentRepository;
import com.bibliotheque.gestion_pret.repository.PenaliteRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;

@RestController
@RequestMapping("/api/adherents")
@Transactional(readOnly = true)
public class AdherentApiController {

    @Autowired
    private AdherentRepository adherentRepository;
    @Autowired
    private PretRepository pretRepository;
    @Autowired
    private PenaliteRepository penaliteRepository;

    @GetMapping
    public List<AdherentSimpleDto> listerTousLesAdherents() {
        return adherentRepository.findAll().stream()
                .map(this::convertToAdherentSimpleDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdherentDetailDto> getAdherentDetails(@PathVariable Long id) {
        return adherentRepository.findById(id)
                .map(this::convertToAdherentDetailDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    private AdherentSimpleDto convertToAdherentSimpleDto(Adherent adherent) {
        AdherentSimpleDto dto = new AdherentSimpleDto();
        dto.setId(adherent.getId());
        dto.setNom(adherent.getNom());
        dto.setPrenom(adherent.getPrenom());
        dto.setEmail(adherent.getEmail());
        dto.setActif(adherent.getActif() != null && adherent.getActif());
        return dto;
    }

    private AdherentDetailDto convertToAdherentDetailDto(Adherent adherent) {
        AdherentDetailDto dto = new AdherentDetailDto();
        dto.setId(adherent.getId());
        dto.setNom(adherent.getNom());
        dto.setPrenom(adherent.getPrenom());
        dto.setEmail(adherent.getEmail());
        dto.setTelephone(adherent.getTelephone());

        if (adherent.getTypeAdherent() != null) {
            dto.setTypeAbonnement(adherent.getTypeAdherent().getNom());
            dto.setQuotaMaxPrets(adherent.getTypeAdherent().getMaxLivresEmprunt());
        }
        dto.setDateAbonnementDebut(adherent.getAbonnementDebut());
        dto.setDateFinAbonnement(adherent.getAbonnementFin());
        if (adherent.getStatutPaiement() != null) {
            dto.setStatutPaiementAbonnement(adherent.getStatutPaiement().name());
        }

        int pretsEnCours = (int) pretRepository.countByAdherent_IdAndStatutPret_Nom(adherent.getId(), "En cours");
        dto.setPretsEnCours(pretsEnCours);
        dto.setQuotaRestant(dto.getQuotaMaxPrets() - pretsEnCours);

        dto.setEstSuspendu(
                adherent.getDateFinSuspension() != null && adherent.getDateFinSuspension().isAfter(LocalDate.now()));
        dto.setDateFinSuspension(adherent.getDateFinSuspension());
        dto.setADesPenalitesFinancieres(penaliteRepository.existsByAdherentIdAndStatutPaiement(adherent.getId(),
                StatutPaiementPenalite.impaye));

        dto.setHistoriquePenalites(
                penaliteRepository.findByAdherentIdOrderByDateCalculDesc(adherent.getId()).stream()
                        .map(this::convertToPenaliteDto)
                        .collect(Collectors.toList()));

        return dto;
    }

    private PenaliteDto convertToPenaliteDto(Penalite penalite) {
        PenaliteDto dto = new PenaliteDto();
        dto.setId(penalite.getId());
        if (penalite.getPret() != null && penalite.getPret().getLivre() != null) {
            dto.setLivreTitre(penalite.getPret().getLivre().getTitre());
        }
        dto.setMontant(penalite.getMontant());
        dto.setJoursRetard(penalite.getJoursRetard());
        dto.setDateCalcul(penalite.getDateCalcul());
        if (penalite.getStatutPaiement() != null) {
            dto.setStatutPaiement(penalite.getStatutPaiement().name());
        }
        return dto;
    }
}