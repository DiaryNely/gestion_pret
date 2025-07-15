-- ==============================================
-- INSERTION DES DONNÉES DE TEST
-- ==============================================

-- Types d'adhérents
INSERT INTO types_adherents (nom, description, max_livres_emprunt, duree_pret_jours) VALUES
('Etudiant', 'Etudiant universitaire', 5, 14),
('Professionnel', 'Professionnel en activité', 8, 21),
('Enseignant', 'Enseignant ou chercheur', 12, 30),
('Admin', 'Administrateur système', 999, 365);

-- Genres littéraires
INSERT INTO genres_litteraires (nom, description) VALUES
('Roman', 'Fiction narrative'),
('Essai', 'Ouvrage de réflexion'),
('Biographie', 'Récit de vie'),
('Science-Fiction', 'Fiction scientifique'),
('Polar', 'Roman policier'),
('Histoire', 'Ouvrage historique'),
('Philosophie', 'Ouvrage philosophique'),
('Informatique', 'Ouvrage technique informatique'),
('Mathématiques', 'Ouvrage mathématique'),
('Littérature classique', 'Classiques de la littérature');

-- Langues
INSERT INTO langues (nom, code_iso) VALUES
('Français', 'FR'),
('Anglais', 'EN'),
('Espagnol', 'ES'),
('Allemand', 'DE'),
('Italien', 'IT'),
('Arabe', 'AR'),
('Chinois', 'ZH'),
('Japonais', 'JA');

-- Emplacements
INSERT INTO emplacements (rayon, etagere, description) VALUES
('A', '1', 'Littérature française'),
('A', '2', 'Littérature étrangère'),
('A', '3', 'Poésie'),
('B', '1', 'Sciences humaines'),
('B', '2', 'Histoire'),
('B', '3', 'Philosophie'),
('C', '1', 'Sciences exactes'),
('C', '2', 'Informatique'),
('C', '3', 'Mathématiques'),
('D', '1', 'Arts et culture'),
('D', '2', 'Biographies'),
('E', '1', 'Ouvrages de référence');

-- Livres
INSERT INTO livres (titre, auteur, date_edition, nombre_exemplaires, mots_cles, langue_id, genre_id, emplacement_id, isbn, editeur, pages) VALUES
('Le Petit Prince', 'Antoine de Saint-Exupéry', '1943-04-06', 3, 'conte, philosophie, enfance', 1, 1, 1, '978-2-07-040835-4', 'Gallimard', 96),
('1984', 'George Orwell', '1949-06-08', 2, 'dystopie, politique, totalitarisme', 2, 4, 2, '978-0-452-28423-4', 'Secker & Warburg', 328),
('Algorithmes et structures de données', 'Thomas H. Cormen', '2009-07-31', 5, 'informatique, algorithmes, programmation', 2, 8, 8, '978-2-10-054526-1', 'MIT Press', 1312),
('Histoire de France', 'Jules Michelet', '1833-01-01', 4, 'histoire, france, moyen-age', 1, 6, 5, '978-2-07-011152-3', 'Flammarion', 890),
('Critique de la raison pure', 'Emmanuel Kant', '1781-05-11', 2, 'philosophie, métaphysique, épistémologie', 1, 7, 6, '978-2-07-075864-2', 'Flammarion', 754),
('Fondation', 'Isaac Asimov', '1951-05-01', 4, 'science-fiction, empire galactique, psychohistoire', 2, 4, 2, '978-0-553-29335-0', 'Gnome Press', 244),
('Les Misérables', 'Victor Hugo', '1862-03-30', 3, 'roman social, paris, révolution', 1, 10, 1, '978-2-07-040070-9', 'Lacroix', 1900),
('Steve Jobs', 'Walter Isaacson', '2011-10-24', 2, 'biographie, apple, innovation', 2, 3, 11, '978-1-4516-4853-9', 'Simon & Schuster', 656),
('Analyse mathématique', 'Jean-Marie Monier', '2018-08-15', 6, 'mathématiques, analyse, fonctions', 1, 9, 9, '978-2-10-077833-1', 'Dunod', 512),
('Le Seigneur des Anneaux', 'J.R.R. Tolkien', '1954-07-29', 3, 'fantasy, aventure, magie', 2, 1, 2, '978-0-547-92822-7', 'Allen & Unwin', 1216);

-- Adhérents
INSERT INTO adherents (nom, prenom, adresse_postale, email, telephone, type_adherent_id, abonnement_type, abonnement_debut, abonnement_fin, statut_paiement, mot_de_passe) VALUES
('Dupont', 'Jean', '123 Rue de la Paix, 75001 Paris', 'jean.dupont@email.com', '0123456789', 1, 'annuel', '2024-01-15', '2026-01-15', 'paye', 'mdp_hashe_123'),
('Martin', 'Marie', '45 Avenue des Champs, 69002 Lyon', 'marie.martin@email.com', '0234567890', 2, 'mensuel', '2024-12-01', '2025-01-01', 'paye', 'mdp_hashe_456'),
('Dubois', 'Pierre', '78 Boulevard Saint-Germain, 75006 Paris', 'pierre.dubois@univ.fr', '0345678901', 3, 'annuel', '2024-09-01', '2025-09-01', 'paye', 'mdp_hashe_789'),
('Leroy', 'Sophie', '12 Rue Victor Hugo, 31000 Toulouse', 'sophie.leroy@email.com', '0456789012', 1, 'mensuel', '2024-11-15', '2024-12-15', 'impaye', 'mdp_hashe_101'),
('Bernard', 'Luc', '34 Cours Lafayette, 13001 Marseille', 'luc.bernard@entreprise.com', '0567890123', 2, 'annuel', '2024-06-01', '2025-06-01', 'paye', 'mdp_hashe_202'),
('Admin', 'Système', '1 Rue de la Bibliothèque, 75001 Paris', 'admin@bibliotheque.fr', '0100000000', 4, 'annuel', '2024-01-01', '2025-01-01', 'paye', 'admin_hashe_999'),
('Moreau', 'Claire', '56 Rue de la République, 67000 Strasbourg', 'claire.moreau@email.com', '0678901234', 1, 'mensuel', '2024-10-01', '2024-11-01', 'en_attente', 'mdp_hashe_303'),
('Petit', 'Antoine', '89 Avenue de la Liberté, 59000 Lille', 'antoine.petit@research.fr', '0789012345', 3, 'annuel', '2024-08-15', '2025-08-15', 'paye', 'mdp_hashe_404');

-- Types de prêts
INSERT INTO types_prets (nom, description) VALUES
('Prêt à domicile', 'Livre emprunté pour sortir de la bibliothèque'),
('Prêt sur place', 'Consultation uniquement dans la bibliothèque');

-- Statuts de prêts
INSERT INTO statuts_prets (nom, description) VALUES
('En cours', 'Prêt actif'),
('Retourné', 'Livre rendu'),
('En retard', 'Prêt dépassé');


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



