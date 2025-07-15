package com.bibliotheque.gestion_pret.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibliotheque.gestion_pret.dto.ExemplaireDto;
import com.bibliotheque.gestion_pret.dto.LivreDto;
import com.bibliotheque.gestion_pret.model.Exemplaire;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.ExemplaireRepository;
import com.bibliotheque.gestion_pret.repository.LivreRepository;

@RestController
@RequestMapping("/api/livres")
public class LivreApiController {

    @Autowired
    private LivreRepository livreRepository;
    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @GetMapping
    public List<LivreDto> listerTousLesLivres() {
        List<Livre> livres = livreRepository.findAll();
        return livres.stream().map(this::convertToLivreDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}/exemplaires")
    public ResponseEntity<List<ExemplaireDto>> listerExemplairesParLivre(@PathVariable Long id) {
        if (!livreRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Exemplaire> exemplaires = exemplaireRepository.findByLivreId(id);
        List<ExemplaireDto> dtos = exemplaires.stream().map(this::convertToExemplaireDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private LivreDto convertToLivreDto(Livre livre) {
        LivreDto dto = new LivreDto();
        dto.setId(livre.getId());
        dto.setTitre(livre.getTitre());
        dto.setAuteur(livre.getAuteur());
        if (livre.getLangue() != null) {
            dto.setLangue(livre.getLangue().getNom());
        }
        if (livre.getGenre() != null) {
            dto.setGenre(livre.getGenre().getNom());
        }
        if (livre.getExemplaires() != null) {
            dto.setExemplaires(
                    livre.getExemplaires().stream().map(this::convertToExemplaireDto).collect(Collectors.toList()));
        }
        return dto;
    }

    private ExemplaireDto convertToExemplaireDto(Exemplaire exemplaire) {
        ExemplaireDto dto = new ExemplaireDto();
        dto.setId(exemplaire.getId());
        dto.setCodeExemplaire(exemplaire.getCodeExemplaire());
        dto.setEtat(exemplaire.getEtat());
        dto.setDateAchat(exemplaire.getDateAchat());
        dto.setDisponible(exemplaire.isDisponible());
        return dto;
    }
}