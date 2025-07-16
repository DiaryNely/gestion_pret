package com.bibliotheque.gestion_pret.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bibliotheque.gestion_pret.repository.JourIndispoRepository;

import jakarta.annotation.PostConstruct;

@Service
public class CalendrierService {

    @Autowired
    private JourIndispoRepository jourIndispoRepository;

    private final Set<LocalDate> cacheJoursIndispo = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void initialiserCache() {
        System.out.println("Chargement des jours indisponibles dans le cache...");
        cacheJoursIndispo.clear();
        cacheJoursIndispo.addAll(jourIndispoRepository.findAllDatesIndispo());
        System.out.println(cacheJoursIndispo.size() + " jours indisponibles chargés.");
    }

    public boolean isJourOuvert(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }

        return !cacheJoursIndispo.contains(date);
    }

    public LocalDate getProchainJourOuvert(LocalDate date) {
        LocalDate dateCourante = date;
        while (!isJourOuvert(dateCourante)) {
            dateCourante = dateCourante.plusDays(1);
        }
        return dateCourante;
    }
}