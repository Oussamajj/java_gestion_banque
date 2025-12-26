package com.banque.model;

import java.sql.Timestamp;

public class Transaction {
    private int idTransaction;
    private int idCompte;
    private String typeTransaction;
    private double montant;
    private Integer idCompteDestinataire;
    private Timestamp dateTransaction;
    private String description;

    public Transaction() {}

    public Transaction(int idCompte, String typeTransaction, double montant, String description) {
        this.idCompte = idCompte;
        this.typeTransaction = typeTransaction;
        this.montant = montant;
        this.description = description;
    }

    public int getIdTransaction() { return idTransaction; }
    public void setIdTransaction(int idTransaction) { this.idTransaction = idTransaction; }
    public int getIdCompte() { return idCompte; }
    public void setIdCompte(int idCompte) { this.idCompte = idCompte; }
    public String getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(String typeTransaction) { this.typeTransaction = typeTransaction; }
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
    public Integer getIdCompteDestinataire() { return idCompteDestinataire; }
    public void setIdCompteDestinataire(Integer idCompteDestinataire) { this.idCompteDestinataire = idCompteDestinataire; }
    public Timestamp getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(Timestamp dateTransaction) { this.dateTransaction = dateTransaction; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
