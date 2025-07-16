-- ==============================================
-- INSERTION DES DONNÉES DE TEST
-- ==============================================

-- Types d'adhérents
INSERT INTO types_adherents (nom, description, max_livres_emprunt, duree_pret_jours) VALUES
('Etudiant', 'Etudiant universitaire', 2, 7),
('Professionnel', 'Professionnel en activité', 4, 12),
('Enseignant', 'Enseignant ou chercheur', 3, 9),
('Admin', 'Administrateur système', 999, 365);

-- Genres littéraires
INSERT INTO genres_litteraires (nom, description) VALUES
('Jeunesse/Fantastique', 'Fiction pour la jeunesse'),
('Philosophie', 'Ouvrage philosophique'),
('Littérature classique', 'Classiques de la littérature');

-- Langues
INSERT INTO langues (nom, code_iso) VALUES
('Français', 'FR');


-- Emplacements
INSERT INTO emplacements (rayon, etagere, description) VALUES
('A', '1', 'Littérature française'),
('A', '2', 'Littérature étrangère'),
('A', '3', 'Poésie'),
('B', '1', 'Sciences humaines'),
('B', '2', 'Histoire'),
('B', '3', 'Philosophie');


-- Livres
INSERT INTO livres (titre, auteur, date_edition, nombre_exemplaires, mots_cles, langue_id, genre_id, emplacement_id, isbn, editeur, pages) VALUES
('Les Miserables','Victor Hugo', '1943-04-06',3,'conte, philosophie, enfance',1,3,1,'9782070409189','Gallimard',100),
('L Entranger','Albert Camus','1943-04-06',2,'conte, philosophie, enfance',1,2,2,'9782070360022','Gallimard',100),
('Harry Potter à l école des sorciers','J.K. Rowling','1943-04-06',1,'conte, philosophie, enfance',1,1,3,'9782070643026','Gallimard',100);

-- Adhérents
INSERT INTO adherents (nom, prenom, adresse_postale, email, telephone, type_adherent_id, abonnement_type, abonnement_debut, abonnement_fin, statut_paiement, mot_de_passe) VALUES
('Admin', 'Système', '1 Rue de la Bibliothèque, 75001 Paris', 'admin@bibliotheque.fr', '0100000000', 4, 'annuel', '2024-01-01', '2026-01-01', 'paye', 'admin_hashe_999'),


('ETU001', 'Amine Bensaïd', '123 Rue de la Paix, 75001 Paris', 'jean.dupont@email.com', '0123456789', 1, 'annuel', '2025-02-01', '2025-07-24', 'paye', 'mdp_hashe_123'),
('ETU002', 'Sarah El Khattabi', '45 Avenue des Champs, 69002 Lyon', 'marie.martin@email.com', '0234567890', 1, 'mensuel', '2025-02-01', '2025-07-01', 'impaye', 'mdp_hashe_456'),
('ETU003', 'Youssef Moujahid', '78 Boulevard Saint-Germain, 75006 Paris', 'pierre.dubois@univ.fr', '0345678901', 1, 'annuel', '2025-04-01', '2025-12-01', 'paye', 'mdp_hashe_789'),
('ENS001', 'Nadia Benali', '12 Rue Victor Hugo, 31000 Toulouse', 'sophie.leroy@email.com', '0456789012', 3, 'mensuel', '2025-07-01', '2026-07-01', 'paye', 'mdp_hashe_101'),
('ENS002', 'Karim Haddadi', '34 Cours Lafayette, 13001 Marseille', 'luc.bernard@entreprise.com', '0567890123', 3, 'annuel', '2025-08-01', '2026-05-01', 'impaye', 'mdp_hashe_202'),
('ENS003', 'Salima Touhami', '56 Rue de la République, 67000 Strasbourg', 'rachid.moreau@email.com', '0678901234', 3, 'mensuel', '2025-07-01', '2026-06-01', 'paye', 'mdp_hashe_303'),
('PROF001', 'Rachid El Mansouri', '56 Rue de la République, 67000 Strasbourg', 'claire.moreau@email.com', '0678901234', 2, 'mensuel', '2025-06-01', '2025-12-01', 'paye', 'mdp_hashe_303'),
('PROF002', 'Amina Zerouali', '89 Avenue de la Liberté, 59000 Lille', 'antoine.petit@research.fr', '0789012345', 2, 'annuel', '2024-10-01', '2025-06-01', 'impaye', 'mdp_hashe_404');

-- Types de prêts
INSERT INTO types_prets (nom, description) VALUES
('Prêt à domicile', 'Livre emprunté pour sortir de la bibliothèque'),
('Prêt sur place', 'Consultation uniquement dans la bibliothèque');

-- Statuts de prêts
INSERT INTO statuts_prets (nom, description) VALUES
('En cours', 'Prêt actif'),
('Retourné', 'Livre rendu'),
('En retard', 'Prêt dépassé');

INSERT INTO jour_indispo(date_indispo,valeur) VALUES
('2025-07-13','Dimanche'),
('2025-07-20','Dimanche'),
('2025-07-27','Dimanche'),
('2025-08-03','Dimanche'),
('2025-08-10','Dimanche'),
('2025-08-17','Dimanche'),

('2025-07-26','Fety be'),
('2025-07-19','Fety kely');


-- Paramètres système
INSERT INTO parametres_systeme (nom, valeur, type_valeur, description, modifiable) VALUES
('penalite_par_jour', '0.50', 'decimal', 'Montant de la pénalité par jour de retard', true),
('jours_tolerance', '2', 'integer', 'Nombre de jours de grâce avant pénalité', true),
('duree_reservation', '7', 'integer', 'Durée en jours de validité d une réservation', true),
('max_prolongations', '2', 'integer', 'Nombre maximum de prolongations par prêt', true),
('duree_prolongation', '7', 'integer', 'Durée en jours d une prolongation', true),
('notification_avant_echeance', '3', 'integer', 'Nombre de jours avant échéance pour notifier', true),
('bibliotheque_nom', 'Bibliothèque Municipale', 'string', 'Nom de la bibliothèque', true),
('bibliotheque_adresse', '1 Place de la Culture, 75001 Paris', 'string', 'Adresse de la bibliothèque', true),
('bibliotheque_telephone', '01.23.45.67.89', 'string', 'Téléphone de la bibliothèque', true),
('bibliotheque_email', 'contact@bibliotheque.fr', 'string', 'Email de la bibliothèque', true),
('ratio_suspension_jour', '2', 'integer', 'Nombre de jours de suspension par jour de retard', true);



