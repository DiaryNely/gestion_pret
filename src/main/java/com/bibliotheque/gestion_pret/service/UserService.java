package com.bibliotheque.gestion_pret.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.repository.LivreRepository;

import jakarta.persistence.criteria.Predicate;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private LivreRepository livreRepository;

    public List<Livre> getCatalogueLivres() {
        return livreRepository.findAll();
    }

    public List<Livre> searchLivres(String query) {
        // Si la recherche est vide ou nulle, on renvoie tout le catalogue
        if (!StringUtils.hasText(query)) {
            return livreRepository.findAll();
        }

        // On utilise notre méthode findAll avec une spécification
        return livreRepository.findAll(getSearchSpecification(query));
    }

    private Specification<Livre> getSearchSpecification(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            // 'root' représente l'entité Livre
            // 'criteriaBuilder' est utilisé pour construire les prédicats (conditions
            // WHERE)

            List<Predicate> predicates = new ArrayList<>();

            // Le terme de recherche (en minuscule pour une recherche insensible à la casse)
            String searchTerm = "%" + query.toLowerCase() + "%";

            // On ajoute une condition pour chaque champ que l'on veut rechercher
            // Le 'OR' combine les différentes conditions :
            // WHERE lower(titre) LIKE '%query%' OR lower(auteur) LIKE '%query%' OR
            // lower(mots_cles) LIKE '%query%'
            Predicate searchPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("titre")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("auteur")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("motsCles")), searchTerm),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), searchTerm) // Ajout de la recherche
                                                                                              // par ISBN
            );

            predicates.add(searchPredicate);

            // On pourrait ajouter d'autres conditions ici, par exemple :
            // Predicate actifPredicate = criteriaBuilder.isTrue(root.get("actif"));
            // predicates.add(actifPredicate);

            // On combine tous les prédicats avec un 'AND'
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}