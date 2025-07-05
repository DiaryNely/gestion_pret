package com.bibliotheque.gestion_pret.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.LivreRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private LivreRepository livreRepository;

    public List<Livre> getCatalogueLivres() {
        return livreRepository.findAll();
    }
}