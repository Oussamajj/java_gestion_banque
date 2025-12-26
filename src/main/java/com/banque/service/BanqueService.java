package com.banque.service;

import com.banque.config.DatabaseConnection;
import com.banque.dao.CompteDAO;
import com.banque.dao.TransactionDAO;
import com.banque.model.Compte;
import com.banque.model.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

public class BanqueService {
    private final CompteDAO compteDAO = new CompteDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public boolean effectuerVirement(int idCompteSource, int idCompteDestinataire, double montant) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Compte source = compteDAO.getCompteById(idCompteSource);
                if (source == null || source.getSolde() < montant) {
                    conn.rollback();
                    return false;
                }

                if (!compteDAO.retirer(conn, idCompteSource, montant)) {
                    conn.rollback();
                    return false;
                }
                if (!compteDAO.deposer(conn, idCompteDestinataire, montant)) {
                    conn.rollback();
                    return false;
                }

                Transaction emise = new Transaction(idCompteSource, "VIREMENT_EMIS", montant, "Virement émis");
                emise.setIdCompteDestinataire(idCompteDestinataire);
                transactionDAO.ajouterTransaction(conn, emise);

                Transaction recue = new Transaction(idCompteDestinataire, "VIREMENT_RECU", montant, "Virement reçu");
                recue.setIdCompteDestinataire(idCompteSource);
                transactionDAO.ajouterTransaction(conn, recue);

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String genererNumeroCompte() {
        return "CPT" + System.currentTimeMillis();
    }
}
