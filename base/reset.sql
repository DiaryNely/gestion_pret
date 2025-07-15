-- Désactiver temporairement les contraintes de clés étrangères pour éviter les erreurs
SET CONSTRAINTS ALL DEFERRED;

-- Supprimer les données des tables dans l'ordre inverse des dépendances
DELETE FROM prolongations;
DELETE FROM penalites;
DELETE FROM reservations;
DELETE FROM prets;
DELETE FROM adherents;
DELETE FROM livres;
DELETE FROM emplacements;
DELETE FROM langues;
DELETE FROM genres_litteraires;
DELETE FROM types_adherents;
DELETE FROM types_prets;
DELETE FROM statuts_prets;
DELETE FROM parametres_systeme;

-- Réinitialiser les séquences pour chaque table
ALTER SEQUENCE prolongations_id_seq RESTART WITH 1;
ALTER SEQUENCE penalites_id_seq RESTART WITH 1;
ALTER SEQUENCE reservations_id_seq RESTART WITH 1;
ALTER SEQUENCE prets_id_seq RESTART WITH 1;
ALTER SEQUENCE adherents_id_seq RESTART WITH 1;
ALTER SEQUENCE livres_id_seq RESTART WITH 1;
ALTER SEQUENCE emplacements_id_seq RESTART WITH 1;
ALTER SEQUENCE langues_id_seq RESTART WITH 1;
ALTER SEQUENCE genres_litteraires_id_seq RESTART WITH 1;
ALTER SEQUENCE types_adherents_id_seq RESTART WITH 1;
ALTER SEQUENCE types_prets_id_seq RESTART WITH 1;
ALTER SEQUENCE statuts_prets_id_seq RESTART WITH 1;
ALTER SEQUENCE parametres_systeme_id_seq RESTART WITH 1;

-- Réactiver les contraintes
SET CONSTRAINTS ALL IMMEDIATE;

-- Optionnel : Vérifier que toutes les tables sont vides
SELECT 'prolongations' AS table_name, COUNT(*) AS count FROM prolongations
UNION
SELECT 'penalites' AS table_name, COUNT(*) AS count FROM penalites
UNION
SELECT 'reservations' AS table_name, COUNT(*) AS count FROM reservations
UNION
SELECT 'prets' AS table_name, COUNT(*) AS count FROM prets
UNION
SELECT 'adherents' AS table_name, COUNT(*) AS count FROM adherents
UNION
SELECT 'livres' AS table_name, COUNT(*) AS count FROM livres
UNION
SELECT 'emplacements' AS table_name, COUNT(*) AS count FROM emplacements
UNION
SELECT 'langues' AS table_name, COUNT(*) AS count FROM langues
UNION
SELECT 'genres_litteraires' AS table_name, COUNT(*) AS count FROM genres_litteraires
UNION
SELECT 'types_adherents' AS table_name, COUNT(*) AS count FROM types_adherents
UNION
SELECT 'types_prets' AS table_name, COUNT(*) AS count FROM types_prets
UNION
SELECT 'statuts_prets' AS table_name, COUNT(*) AS count FROM statuts_prets
UNION
SELECT 'parametres_systeme' AS table_name, COUNT(*) AS count FROM parametres_systeme;