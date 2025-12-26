CREATE DATABASE IF NOT EXISTS gestion_banque;
USE gestion_banque;

CREATE TABLE IF NOT EXISTS clients (
    id_client INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comptes (
    id_compte INT PRIMARY KEY AUTO_INCREMENT,
    numero_compte VARCHAR(20) UNIQUE NOT NULL,
    id_client INT NOT NULL,
    type_compte ENUM('COURANT', 'PROFESSIONNEL', 'EPARGNE') NOT NULL,
    solde DECIMAL(15, 2) DEFAULT 0.00,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut ENUM('ACTIF', 'FERME') DEFAULT 'ACTIF',
    FOREIGN KEY (id_client) REFERENCES clients(id_client) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    id_transaction INT PRIMARY KEY AUTO_INCREMENT,
    id_compte INT NOT NULL,
    type_transaction ENUM('DEPOT', 'RETRAIT', 'VIREMENT_EMIS', 'VIREMENT_RECU') NOT NULL,
    montant DECIMAL(15, 2) NOT NULL,
    id_compte_destinataire INT,
    date_transaction TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    FOREIGN KEY (id_compte) REFERENCES comptes(id_compte) ON DELETE CASCADE,
    FOREIGN KEY (id_compte_destinataire) REFERENCES comptes(id_compte) ON DELETE SET NULL
);
