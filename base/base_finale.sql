CREATE TABLE types_adherents (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    max_livres_emprunt INTEGER DEFAULT 5,
    duree_pret_jours INTEGER DEFAULT 14,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Colonnes ajoutées via ALTER TABLE
    max_reservations_actives INTEGER NOT NULL DEFAULT 1,
    duree_prolongation_jours INTEGER NOT NULL DEFAULT 3,
    duree_suspension_retard_jours INTEGER NOT NULL DEFAULT 7
);


CREATE TABLE livres (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    date_edition DATE,
    nombre_exemplaires INTEGER DEFAULT 1,
    mots_cles TEXT,
    langue_id INTEGER REFERENCES langues(id),
    genre_id INTEGER REFERENCES genres_litteraires(id),
    emplacement_id INTEGER REFERENCES emplacements(id),
    isbn VARCHAR(20),
    editeur VARCHAR(255),
    pages INTEGER,
    restriction_type_adherent INTEGER REFERENCES types_adherents(id) DEFAULT NULL,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contrainte ajoutée via ALTER TABLE
    CONSTRAINT check_nombre_exemplaires CHECK (nombre_exemplaires > 0)
);



CREATE TABLE adherents (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    adresse_postale TEXT,
    email VARCHAR(255) UNIQUE,
    telephone VARCHAR(20),
    type_adherent_id INTEGER REFERENCES types_adherents(id) NOT NULL,
    date_inscription DATE DEFAULT CURRENT_DATE,
    abonnement_type VARCHAR(20) CHECK (abonnement_type IN ('mensuel', 'annuel')),
    abonnement_debut DATE,
    abonnement_fin DATE,
    statut_paiement VARCHAR(20) DEFAULT 'impaye' CHECK (statut_paiement IN ('paye', 'impaye', 'en_attente')),
    mot_de_passe VARCHAR(255),
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Contrainte ajoutée via ALTER TABLE
    CONSTRAINT check_dates_abonnement CHECK (abonnement_fin IS NULL OR abonnement_fin > abonnement_debut)
);


CREATE TABLE prets (
    id SERIAL PRIMARY KEY,
    livre_id INTEGER REFERENCES livres(id) NOT NULL,
    adherent_id INTEGER REFERENCES adherents(id) NOT NULL,
    date_emprunt DATE DEFAULT CURRENT_DATE,
    date_retour_prevue DATE NOT NULL,
    date_retour_reelle DATE,
    type_pret_id INTEGER REFERENCES types_prets(id) NOT NULL,
    statut_pret_id INTEGER REFERENCES statuts_prets(id) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Contraintes ajoutées via ALTER TABLE
    CONSTRAINT check_dates_pret CHECK (date_retour_prevue > date_emprunt),
    CONSTRAINT check_date_retour_reelle CHECK (date_retour_reelle IS NULL OR date_retour_reelle >= date_emprunt)
);


CREATE TABLE penalites (
    id SERIAL PRIMARY KEY,
    pret_id INTEGER REFERENCES prets(id) NOT NULL,
    adherent_id INTEGER REFERENCES adherents(id) NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    jours_retard INTEGER NOT NULL,
    date_calcul DATE DEFAULT CURRENT_DATE,
    statut_paiement VARCHAR(20) DEFAULT 'impaye' CHECK (statut_paiement IN ('paye', 'impaye', 'remise')),
    date_paiement DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Contrainte ajoutée via ALTER TABLE
    CONSTRAINT check_montant_penalite CHECK (montant >= 0)
);


CREATE TABLE genres_litteraires (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE langues (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    code_iso VARCHAR(3) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE emplacements (
    id SERIAL PRIMARY KEY,
    rayon VARCHAR(20) NOT NULL,
    etagere VARCHAR(20) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(rayon, etagere)
);


CREATE TABLE types_prets (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE statuts_prets (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    livre_id INTEGER REFERENCES livres(id) NOT NULL,
    adherent_id INTEGER REFERENCES adherents(id) NOT NULL,
    date_reservation DATE DEFAULT CURRENT_DATE,
    date_expiration DATE,
    statut VARCHAR(20) DEFAULT 'active' CHECK (statut IN ('active', 'terminee', 'annulee')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE parametres_systeme (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    valeur TEXT NOT NULL,
    type_valeur VARCHAR(20) DEFAULT 'string' CHECK (type_valeur IN ('string', 'integer', 'decimal', 'boolean', 'date')),
    description TEXT,
    modifiable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE prolongations (
    id SERIAL PRIMARY KEY,
    pret_id INTEGER REFERENCES prets(id) NOT NULL,
    ancienne_date_retour DATE NOT NULL,
    nouvelle_date_retour DATE NOT NULL,
    motif TEXT,
    demande_par INTEGER REFERENCES adherents(id),
    approuve_par INTEGER REFERENCES adherents(id),
    date_demande DATE DEFAULT CURRENT_DATE,
    date_approbation DATE,
    statut VARCHAR(20) DEFAULT 'en_attente' CHECK (statut IN ('en_attente', 'approuvee', 'refusee')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE jour_indispo(
    id SERIAL PRIMARY KEY,
    date_indispo DATE NOT NULL,
    valeur VARCHAR(100)
);