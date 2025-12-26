package com.banque.dao;

import com.banque.config.DatabaseConnection;
import com.banque.model.Compte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompteDAO {

    public boolean creerCompte(Compte compte) {
        String sql = "INSERT INTO comptes (numero_compte, id_client, type_compte, solde) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, compte.getNumeroCompte());
            pstmt.setInt(2, compte.getIdClient());
            pstmt.setString(3, compte.getTypeCompte());
            pstmt.setDouble(4, compte.getSolde());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Compte> getComptesByClient(int idClient) {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM comptes WHERE id_client = ? ORDER BY statut DESC, id_compte";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Compte compte = new Compte();
                compte.setIdCompte(rs.getInt("id_compte"));
                compte.setNumeroCompte(rs.getString("numero_compte"));
                compte.setIdClient(rs.getInt("id_client"));
                compte.setTypeCompte(rs.getString("type_compte"));
                compte.setSolde(rs.getDouble("solde"));
                compte.setStatut(rs.getString("statut"));
                comptes.add(compte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comptes;
    }

    public Compte getCompteById(int id) {
        String sql = "SELECT * FROM comptes WHERE id_compte = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Compte compte = new Compte();
                compte.setIdCompte(rs.getInt("id_compte"));
                compte.setNumeroCompte(rs.getString("numero_compte"));
                compte.setIdClient(rs.getInt("id_client"));
                compte.setTypeCompte(rs.getString("type_compte"));
                compte.setSolde(rs.getDouble("solde"));
                compte.setStatut(rs.getString("statut"));
                return compte;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Compte getCompteByNumero(String numero) {
        String sql = "SELECT * FROM comptes WHERE numero_compte = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numero);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Compte compte = new Compte();
                compte.setIdCompte(rs.getInt("id_compte"));
                compte.setNumeroCompte(rs.getString("numero_compte"));
                compte.setIdClient(rs.getInt("id_client"));
                compte.setTypeCompte(rs.getString("type_compte"));
                compte.setSolde(rs.getDouble("solde"));
                compte.setStatut(rs.getString("statut"));
                return compte;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deposer(int idCompte, double montant) {
        String sql = "UPDATE comptes SET solde = solde + ? WHERE id_compte = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idCompte);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean retirer(int idCompte, double montant) {
        String sql = "UPDATE comptes SET solde = solde - ? WHERE id_compte = ? AND solde >= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idCompte);
            pstmt.setDouble(3, montant);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimerCompte(int idCompte) {
        String sql = "UPDATE comptes SET statut = 'FERME' WHERE id_compte = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCompte);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Transaction-aware operations
    public boolean deposer(Connection conn, int idCompte, double montant) throws SQLException {
        String sql = "UPDATE comptes SET solde = solde + ? WHERE id_compte = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idCompte);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean retirer(Connection conn, int idCompte, double montant) throws SQLException {
        String sql = "UPDATE comptes SET solde = solde - ? WHERE id_compte = ? AND solde >= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, montant);
            pstmt.setInt(2, idCompte);
            pstmt.setDouble(3, montant);
            return pstmt.executeUpdate() > 0;
        }
    }
}
