package com.banque.dao;

import com.banque.config.DatabaseConnection;
import com.banque.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean ajouterTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (id_compte, type_transaction, montant, id_compte_destinataire, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getIdCompte());
            pstmt.setString(2, transaction.getTypeTransaction());
            pstmt.setDouble(3, transaction.getMontant());
            if (transaction.getIdCompteDestinataire() != null) {
                pstmt.setInt(4, transaction.getIdCompteDestinataire());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, transaction.getDescription());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> getHistoriqueCompte(int idCompte) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE id_compte = ? OR id_compte_destinataire = ? ORDER BY date_transaction DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCompte);
            pstmt.setInt(2, idCompte);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setIdTransaction(rs.getInt("id_transaction"));
                transaction.setIdCompte(rs.getInt("id_compte"));
                transaction.setTypeTransaction(rs.getString("type_transaction"));
                transaction.setMontant(rs.getDouble("montant"));
                int dest = rs.getInt("id_compte_destinataire");
                transaction.setIdCompteDestinataire(rs.wasNull() ? null : dest);
                transaction.setDateTransaction(rs.getTimestamp("date_transaction"));
                transaction.setDescription(rs.getString("description"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Transaction-aware add
    public boolean ajouterTransaction(Connection conn, Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (id_compte, type_transaction, montant, id_compte_destinataire, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaction.getIdCompte());
            pstmt.setString(2, transaction.getTypeTransaction());
            pstmt.setDouble(3, transaction.getMontant());
            if (transaction.getIdCompteDestinataire() != null) {
                pstmt.setInt(4, transaction.getIdCompteDestinataire());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, transaction.getDescription());
            return pstmt.executeUpdate() > 0;
        }
    }
}
