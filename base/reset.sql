-- DÉBUTE UNE TRANSACTION. TOUT CE QUI SUIT SERA EXÉCUTÉ COMME UN SEUL BLOC.
-- SI UNE COMMANDE ÉCHOUE, TOUT SERA ANNULÉ (ROLLBACK).
BEGIN;

-- Désactive temporairement TOUTES les contraintes de déclenchement (triggers).
-- C'est une sécurité supplémentaire pour que rien ne s'exécute pendant qu'on vide les tables.
SET session_replication_role = 'replica';

-- Vide toutes les tables spécifiées en une seule commande.
-- TRUNCATE est plus rapide que DELETE et gère mieux les dépendances.
-- L'ordre n'est plus aussi critique grâce à la désactivation des triggers.
-- On ajoute toutes les tables, même les nouvelles comme 'exemplaires'.
TRUNCATE TABLE 
    prolongations,
    penalites,
    reservations,
    prets,
    exemplaires, -- Ajout de la nouvelle table
    livres,
    adherents,
    emplacements,
    langues,
    genres_litteraires,
    types_adherents,
    types_prets,
    statuts_prets,
    parametres_systeme,
    jour_indispo, -- Ajout de la nouvelle table
    demande_prolongation -- Si vous avez une table avec ce nom
    -- Ajoutez ici toute autre table que vous auriez pu créer
RESTART IDENTITY -- Réinitialise toutes les séquences (les ID recommenceront à 1)
CASCADE;         -- Vide aussi les tables qui dépendent de celles-ci (sécurité)

-- Réactive les contraintes et les triggers.
SET session_replication_role = 'origin';

-- VALIDE LA TRANSACTION. TOUTES LES MODIFICATIONS SONT MAINTENANT PERMANENTES.
COMMIT;

-- Message de confirmation final
SELECT 'Toutes les tables ont été réinitialisées avec succès !' AS status;