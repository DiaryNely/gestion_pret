-- On suppose que les adhérents et les livres existent déjà.
-- On va créer des prêts en retard pour justifier les pénalités.

-- Prêt en retard pour l'adhérent ETU001 (id=2) sur le livre "1984" (id=2)
-- Rendu avec 5 jours de retard.
INSERT INTO prets (livre_id, adherent_id, type_pret_id, statut_pret_id, date_emprunt, date_retour_prevue, date_retour_reelle) 
VALUES (1, 2, 1, 2, '2024-06-01', '2024-06-08', '2024-06-13');

-- Prêt en retard pour l'adhérent ENS001 (id=5) sur le livre "Dune" (supposons id=11)
-- Rendu avec 10 jours de retard.
INSERT INTO prets (livre_id, adherent_id, type_pret_id, statut_pret_id, date_emprunt, date_retour_prevue, date_retour_reelle) 
VALUES (2, 5, 1, 2, '2024-05-20', '2024-05-29', '2024-06-08');

-- Prêt en retard pour l'adhérent PROF001 (id=8) sur le livre "Les Misérables" (id=7)
-- Rendu avec 3 jours de retard.
INSERT INTO prets (livre_id, adherent_id, type_pret_id, statut_pret_id, date_emprunt, date_retour_prevue, date_retour_reelle) 
VALUES (3, 8, 2, 2, '2024-06-10', '2024-06-22', '2024-06-25');


-- 5 jours de retard à 0.50 par jour = 2.50
INSERT INTO penalites (pret_id, adherent_id, montant, jours_retard, statut_paiement)
VALUES (
    (SELECT id FROM prets WHERE adherent_id = 2 AND livre_id = 2), -- Trouve le bon prêt_id dynamiquement
    2, -- adherent_id de ETU001
    2.50, 
    5, 
    'impaye'
);

-- Pénalité pour le prêt de ENS001 (supposons que c'est le prêt avec id=2)
-- 10 jours de retard à 0.50 par jour = 5.00
INSERT INTO penalites (pret_id, adherent_id, montant, jours_retard, statut_paiement)
VALUES (
    (SELECT id FROM prets WHERE adherent_id = 5 AND livre_id = 11), -- Trouve le bon prêt_id
    5, -- adherent_id de ENS001
    5.00, 
    10, 
    'impaye'
);

-- Pénalité pour le prêt de PROF001 (supposons que c'est le prêt avec id=3)
-- 3 jours de retard à 0.50 par jour = 1.50
-- On va la marquer comme 'paye' pour l'exemple.
INSERT INTO penalites (pret_id, adherent_id, montant, jours_retard, statut_paiement, date_paiement, notes)
VALUES (
    (SELECT id FROM prets WHERE adherent_id = 8 AND livre_id = 7), -- Trouve le bon prêt_id
    8, -- adherent_id de PROF001
    1.50, 
    3, 
    'paye',
    '2024-06-26', -- Une date de paiement
    'Payé en espèces à l''accueil.' -- Une note de l'admin
);