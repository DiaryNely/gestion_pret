package com.bibliotheque.gestion_pret.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bibliotheque.gestion_pret.model.Adherent;
import com.bibliotheque.gestion_pret.model.Livre;
import com.bibliotheque.gestion_pret.model.Pret;

@Repository
public interface PretRepository extends JpaRepository<Pret, Long> {

    long countByStatutPret_Nom(String statutNom);

    List<Pret> findTop5ByOrderByDateEmpruntDesc();

    List<Pret> findByStatutPret_Nom(String statutNom);

    @Override
    @EntityGraph(attributePaths = { "livre", "adherent", "statutPret", "typePret" })
    List<Pret> findAll();

    long countByAdherent_IdAndStatutPret_Nom(Long adherentId, String statutNom);

    @EntityGraph(attributePaths = { "livre", "statutPret" })
    List<Pret> findByAdherent_IdOrderByDateEmpruntDesc(Long adherentId);

    boolean existsByLivreAndAdherentAndDateRetourReelleIsNull(Livre livre, Adherent adherent);

    long countByLivreIdAndDateRetourReelleIsNull(Long livreId);
}
