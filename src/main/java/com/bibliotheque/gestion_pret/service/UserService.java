package com.bibliotheque.gestion_pret.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Pret;
import com.bibliotheque.gestion_pret.repository.LivreRepository;
import com.bibliotheque.gestion_pret.repository.PretRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private PretRepository pretRepository;

    public List<Livre> getCatalogueLivres() {
        return livreRepository.findAll();
    }

    public List<Livre> searchLivres(String query) {
        if (!StringUtils.hasText(query)) {
            return livreRepository.findAll();
        }

        return livreRepository.findAll(getSearchSpecification(query));
    }

    private Specification<Livre> getSearchSpecification(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            String searchTerm = "%" + query.toLowerCase() + "%";

            Predicate searchPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("titre")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("auteur")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("motsCles")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), searchTerm));

            predicates.add(searchPredicate);

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public List<Pret> getPretsByAdherentId(Long adherentId) {
        return pretRepository.findByAdherent_IdOrderByDateEmpruntDesc(adherentId);
    }
}