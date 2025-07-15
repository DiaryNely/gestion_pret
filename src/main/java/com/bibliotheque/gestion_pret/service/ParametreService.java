package com.bibliotheque.gestion_pret.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotheque.gestion_pret.model.ParametreSysteme;
import com.bibliotheque.gestion_pret.repository.ParametreSystemeRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ParametreService {

    @Autowired
    private ParametreSystemeRepository parametreRepository;

    private final Map<String, String> cacheParametres = new ConcurrentHashMap<>();

    @PostConstruct
    public void initialiserCache() {
        System.out.println("Initialisation du cache des paramètres système...");
        List<ParametreSysteme> parametres = parametreRepository.findAll();
        cacheParametres.clear();
        cacheParametres.putAll(
                parametres.stream()
                        .collect(Collectors.toMap(ParametreSysteme::getNom, ParametreSysteme::getValeur)));
        System.out.println(cacheParametres.size() + " paramètres chargés.");
    }

    public BigDecimal getPenaliteParJour() {
        return new BigDecimal(getValeur("penalite_par_jour", "0.20"));
    }

    public int getJoursTolerance() {
        return getValeurAsInteger("jours_tolerance", 0);
    }

    public int getDureeReservationJours() {
        return getValeurAsInteger("duree_reservation", 7);
    }

    public int getRatioSuspensionParJourRetard() {
        return getValeurAsInteger("ratio_suspension_jour", 2);
    }

    private String getValeur(String nom, String valeurParDefaut) {
        return cacheParametres.getOrDefault(nom, valeurParDefaut);
    }

    private Integer getValeurAsInteger(String nom, Integer valeurParDefaut) {
        try {
            return Integer.valueOf(getValeur(nom, valeurParDefaut.toString()));
        } catch (NumberFormatException e) {
            System.err.println("Erreur de parsing pour le paramètre '" + nom
                    + "'. Utilisation de la valeur par défaut: " + valeurParDefaut);
            return valeurParDefaut;
        }
    }
}