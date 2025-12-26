package com.banque.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void init() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Clients avec mot de passe hashé
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS clients (" +
                "id_client INT AUTO_INCREMENT PRIMARY KEY, " +
                "nom VARCHAR(100) NOT NULL, " +
                "prenom VARCHAR(100) NOT NULL, " +
                "email VARCHAR(150) NOT NULL, " +
                "telephone VARCHAR(30) NOT NULL, " +
                "adresse VARCHAR(255) NULL, " +
                "password_hash VARCHAR(255) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
            try (Statement s2 = conn.createStatement()) {
                s2.executeUpdate("ALTER TABLE clients ADD CONSTRAINT uk_clients_email UNIQUE (email)");
            } catch (SQLException ignored) {}
            try (Statement s3 = conn.createStatement()) {
                s3.executeUpdate("ALTER TABLE clients ADD COLUMN password_hash VARCHAR(255) NOT NULL");
            } catch (SQLException ignored) {}

            // Comptes
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS comptes (" +
                "id_compte INT AUTO_INCREMENT PRIMARY KEY, " +
                "numero_compte VARCHAR(50) NOT NULL, " +
                "id_client INT NOT NULL, " +
                "type_compte VARCHAR(50) NOT NULL, " +
                "solde DOUBLE NOT NULL DEFAULT 0, " +
                "statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF', " +
                "CONSTRAINT fk_comptes_clients FOREIGN KEY (id_client) REFERENCES clients(id_client) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );

            // Transactions
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "id_transaction INT AUTO_INCREMENT PRIMARY KEY, " +
                "id_compte INT NOT NULL, " +
                "type_transaction VARCHAR(50) NOT NULL, " +
                "montant DOUBLE NOT NULL, " +
                "id_compte_destinataire INT NULL, " +
                "date_transaction TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "description VARCHAR(255) NULL, " +
                "CONSTRAINT fk_transactions_comptes FOREIGN KEY (id_compte) REFERENCES comptes(id_compte) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
            );
        }
    }
}
