package com.banque.model;

public class Compte {
    private int idCompte;
    private String numeroCompte;
    private int idClient;
    private String typeCompte;
    private double solde;
    private String statut;

    public Compte() {}

    public Compte(String numeroCompte, int idClient, String typeCompte) {
        this.numeroCompte = numeroCompte;
        this.idClient = idClient;
        this.typeCompte = typeCompte;
        this.solde = 0.0;
        this.statut = "ACTIF";
    }

    public int getIdCompte() { return idCompte; }
    public void setIdCompte(int idCompte) { this.idCompte = idCompte; }
    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }
    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }
    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }
    public double getSolde() { return solde; }
    public void setSolde(double solde) { this.solde = solde; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
