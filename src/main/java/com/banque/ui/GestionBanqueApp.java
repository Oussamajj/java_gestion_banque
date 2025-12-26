package com.banque.ui;

import com.banque.dao.ClientDAO;
import com.banque.dao.CompteDAO;
import com.banque.dao.TransactionDAO;
import com.banque.model.Client;
import com.banque.model.Compte;
import com.banque.model.Transaction;
import com.banque.service.BanqueService;
import com.banque.config.DatabaseInitializer;
import com.banque.util.PasswordUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class GestionBanqueApp extends Application {

    private final ClientDAO clientDAO = new ClientDAO();
    private final CompteDAO compteDAO = new CompteDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BanqueService banqueService = new BanqueService();

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseInitializer.init();
            // Retiré le showAlert automatique au démarrage pour plus de fluidité, 
            // mais vous pouvez le remettre si nécessaire.
        } catch (Exception ex) {
            showError("Erreur BD", "Initialisation échouée: " + ex.getMessage());
        }

        TabPane tabPane = new TabPane();

        Tab tabClient = new Tab("Clients", creerInterfaceClient());
        Tab tabCompte = new Tab("Comptes", creerInterfaceCompte());
        Tab tabOperations = new Tab("Opérations", creerInterfaceOperations());
        Tab tabHistorique = new Tab("Historique", creerInterfaceHistorique());

        tabClient.setClosable(false);
        tabCompte.setClosable(false);
        tabOperations.setClosable(false);
        tabHistorique.setClosable(false);

        tabPane.getTabs().addAll(tabClient, tabCompte, tabOperations, tabHistorique);

        Scene scene = new Scene(tabPane, 1100, 750); // Légèrement plus grand pour le confort
        try {
            String css = getClass().getResource("/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Note: style.css non chargé.");
        }

        primaryStage.setTitle("G-Bank | Système de Gestion Bancaire Professionnel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==========================================
    // INTERFACE : GESTION CLIENTS
    // ==========================================
    private VBox creerInterfaceClient() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setMaxWidth(600);
        
        Label titre = new Label("Enregistrement Client");
        titre.getStyleClass().add("header-title");

        GridPane form = new GridPane();
        form.setHgap(15); form.setVgap(15);

        TextField tfNom = new TextField();
        TextField tfPrenom = new TextField();
        TextField tfEmail = new TextField();
        TextField tfTelephone = new TextField();
        TextField tfAdresse = new TextField();
        PasswordField pfPassword = new PasswordField();
        PasswordField pfConfirm = new PasswordField();

        form.add(new Label("Nom:"), 0, 0); form.add(tfNom, 1, 0);
        form.add(new Label("Prénom:"), 0, 1); form.add(tfPrenom, 1, 1);
        form.add(new Label("Email:"), 0, 2); form.add(tfEmail, 1, 2);
        form.add(new Label("Téléphone:"), 0, 3); form.add(tfTelephone, 1, 3);
        form.add(new Label("Adresse:"), 0, 4); form.add(tfAdresse, 1, 4);
        form.add(new Label("Mot de passe:"), 0, 5); form.add(pfPassword, 1, 5);
        form.add(new Label("Confirmer:"), 0, 6); form.add(pfConfirm, 1, 6);

        Button btnAjouter = new Button("CRÉER LE CLIENT");
        btnAjouter.getStyleClass().addAll("button", "btn-primary");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        
        btnAjouter.setOnAction(e -> {
            if (tfNom.getText().isEmpty() || pfPassword.getText().isEmpty()) {
                showError("Champs manquants", "Veuillez remplir les informations obligatoires.");
                return;
            }
            if (!pfPassword.getText().equals(pfConfirm.getText())) {
                showError("Erreur", "Les mots de passe ne correspondent pas.");
                return;
            }

            Client client = new Client(tfNom.getText(), tfPrenom.getText(), tfEmail.getText(), tfTelephone.getText(), tfAdresse.getText());
            client.setPasswordHash(PasswordUtil.hashPassword(pfPassword.getText()));

            if (clientDAO.creerClient(client)) {
                showAlert("Succès", "Client créé avec succès!");
                tfNom.clear(); tfPrenom.clear(); tfEmail.clear(); pfPassword.clear(); pfConfirm.clear();
            }
        });

        card.getChildren().addAll(titre, form, btnAjouter);
        container.getChildren().add(card);
        return container;
    }

    // ==========================================
    // INTERFACE : GESTION COMPTES
    // ==========================================
    private VBox creerInterfaceCompte() {
        VBox container = new VBox(25);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        HBox cardsRow = new HBox(30);
        cardsRow.setAlignment(Pos.TOP_CENTER);

        // Carte Ouverture
        VBox createCard = new VBox(15);
        createCard.getStyleClass().add("card");
        createCard.setPrefWidth(400);

        Label titreC = new Label("Ouverture de Compte");
        titreC.getStyleClass().add("header-title");

        GridPane formC = new GridPane();
        formC.setHgap(10); formC.setVgap(15);
        TextField tfIdClient = new TextField();
        PasswordField pfClientPassword = new PasswordField();
        ComboBox<String> cbTypeCompte = new ComboBox<>();
        cbTypeCompte.getItems().addAll("COURANT", "PROFESSIONNEL", "EPARGNE");
        cbTypeCompte.setValue("COURANT");

        formC.add(new Label("ID Client:"), 0, 0); formC.add(tfIdClient, 1, 0);
        formC.add(new Label("Type:"), 0, 1); formC.add(cbTypeCompte, 1, 1);
        formC.add(new Label("Pass Client:"), 0, 2); formC.add(pfClientPassword, 1, 2);

        Button btnCreer = new Button("GÉNÉRER COMPTE");
        btnCreer.getStyleClass().addAll("button", "btn-success");
        btnCreer.setMaxWidth(Double.MAX_VALUE);
        btnCreer.setOnAction(e -> {
            try {
                int id = Integer.parseInt(tfIdClient.getText());
                if (clientDAO.verifierMotDePasse(id, pfClientPassword.getText())) {
                    String numero = banqueService.genererNumeroCompte();
                    if (compteDAO.creerCompte(new Compte(numero, id, cbTypeCompte.getValue()))) {
                        showAlert("Succès", "Nouveau compte ouvert : " + numero);
                    }
                } else showError("Erreur", "Mot de passe client incorrect.");
            } catch (Exception ex) { showError("Erreur", "Saisie invalide."); }
        });
        createCard.getChildren().addAll(titreC, formC, btnCreer);

        // Carte Clôture
        VBox deleteCard = new VBox(15);
        deleteCard.getStyleClass().add("card");
        deleteCard.setPrefWidth(400);
        Label titreD = new Label("Clôture de Compte");
        titreD.getStyleClass().add("header-title");
        titreD.setStyle("-fx-text-fill: #c0392b;");

        TextField tfIdCompteSuppr = new TextField();
        PasswordField pfPassSuppr = new PasswordField();
        Button btnSupprimer = new Button("FERMER DÉFINITIVEMENT");
        btnSupprimer.getStyleClass().addAll("button", "btn-danger");
        btnSupprimer.setMaxWidth(Double.MAX_VALUE);

        btnSupprimer.setOnAction(e -> {
            try {
                int idCompte = Integer.parseInt(tfIdCompteSuppr.getText());
                Compte c = compteDAO.getCompteById(idCompte);
                if (c != null && clientDAO.verifierMotDePasse(c.getIdClient(), pfPassSuppr.getText())) {
                    if (compteDAO.supprimerCompte(idCompte)) showAlert("Succès", "Le compte a été fermé.");
                } else showError("Erreur", "Authentification échouée.");
            } catch (Exception ex) { showError("Erreur", "ID invalide."); }
        });

        deleteCard.getChildren().addAll(titreD, new Label("ID Compte :"), tfIdCompteSuppr, new Label("Pass Client :"), pfPassSuppr, btnSupprimer);
        cardsRow.getChildren().addAll(createCard, deleteCard);
        container.getChildren().add(cardsRow);
        return container;
    }

    // ==========================================
    // INTERFACE : OPÉRATIONS
    // ==========================================
    private VBox creerInterfaceOperations() {
        VBox container = new VBox(25);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("Opérations Bancaires");
        titre.getStyleClass().add("header-title");

        HBox flow = new HBox(20);
        flow.setAlignment(Pos.CENTER);

        flow.getChildren().addAll(
            creerCarteOp("DÉPÔT", "btn-success", "Entrée de fonds", true),
            creerCarteOp("RETRAIT", "btn-warning", "Sortie de fonds", true),
            creerCarteOp("VIREMENT", "btn-info", "Transfert interne", false)
        );

        container.getChildren().addAll(titre, flow);
        return container;
    }

    private VBox creerCarteOp(String type, String style, String desc, boolean isSimple) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(300);
        
        Label t = new Label(type); t.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        TextField tf1 = new TextField(); tf1.setPromptText(isSimple ? "Numéro Compte" : "ID Source");
        TextField tf2 = new TextField(); tf2.setPromptText("ID Destination");
        TextField tfM = new TextField(); tfM.setPromptText("Montant (MAD)");
        
        VBox inputs = new VBox(10);
        if (isSimple) inputs.getChildren().addAll(tf1, tfM);
        else inputs.getChildren().addAll(tf1, tf2, tfM);

        Button btn = new Button("VALIDER");
        btn.getStyleClass().addAll("button", style);
        btn.setMaxWidth(Double.MAX_VALUE);
        
        btn.setOnAction(e -> {
            try {
                double m = Double.parseDouble(tfM.getText());
                if (isSimple) {
                    Compte c = compteDAO.getCompteByNumero(tf1.getText());
                    if (c != null && !"FERME".equalsIgnoreCase(c.getStatut())) {
                        boolean ok = type.equals("DÉPÔT") ? compteDAO.deposer(c.getIdCompte(), m) : compteDAO.retirer(c.getIdCompte(), m);
                        if (ok) {
                            transactionDAO.ajouterTransaction(new Transaction(c.getIdCompte(), type, m, desc));
                            showAlert("Succès", "Opération terminée.");
                        }
                    } else showError("Erreur", "Compte inexistant ou fermé.");
                } else {
                    if (banqueService.effectuerVirement(Integer.parseInt(tf1.getText()), Integer.parseInt(tf2.getText()), m))
                        showAlert("Succès", "Virement réussi.");
                }
            } catch (Exception ex) { showError("Erreur", "Saisie invalide."); }
        });

        card.getChildren().addAll(t, new Label(desc), new Separator(), inputs, btn);
        return card;
    }

    // ==========================================
    // INTERFACE : HISTORIQUE (CORRIGÉ STATUT)
    // ==========================================
    @SuppressWarnings("unchecked")
	private VBox creerInterfaceHistorique() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        Label titre = new Label("Journal & Consultations");
        titre.getStyleClass().add("header-title");

        HBox mainHBox = new HBox(20);
        
        // --- Colonne Gauche : Liste des comptes d'un client ---
        VBox leftCol = new VBox(15);
        leftCol.getStyleClass().add("card");
        leftCol.setPrefWidth(450);
        
        TextField tfClientSearch = new TextField(); tfClientSearch.setPromptText("Saisir ID Client");
        Button btnFind = new Button("AFFICHER SES COMPTES");
        btnFind.getStyleClass().add("btn-info");
        btnFind.setMaxWidth(Double.MAX_VALUE);
        
        TextArea taResult = new TextArea();
        taResult.setEditable(false);
        taResult.setPrefHeight(400);
        taResult.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13px;");

        btnFind.setOnAction(e -> {
            try {
                List<Compte> comptes = compteDAO.getComptesByClient(Integer.parseInt(tfClientSearch.getText()));
                if (comptes.isEmpty()) taResult.setText("Aucun compte trouvé.");
                else {
                    StringBuilder sb = new StringBuilder("LISTE DES COMPTES :\n====================\n");
                    for (Compte c : comptes) {
                        // Utilisation du champ 'statut' de votre modèle
                        String s = (c.getStatut() != null) ? c.getStatut() : "ACTIF";
                        String icone = s.equalsIgnoreCase("ACTIF") ? " [OK] " : " [!] ";
                        
                        sb.append(icone).append("N°: ").append(c.getNumeroCompte())
                          .append("\n    Type: ").append(c.getTypeCompte())
                          .append("\n    Solde: ").append(String.format("%.2f MAD", c.getSolde()))
                          .append("\n    Statut: ").append(s).append("\n")
                          .append("--------------------\n");
                    }
                    taResult.setText(sb.toString());
                }
            } catch (Exception ex) { taResult.setText("Erreur: ID invalide."); }
        });
        leftCol.getChildren().addAll(new Label("Recherche par Client"), tfClientSearch, btnFind, taResult);

        // --- Colonne Droite : Table des Transactions ---
        VBox rightCol = new VBox(15);
        rightCol.getStyleClass().add("card");
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        
        HBox searchRow = new HBox(10);
        TextField tfCompteHisto = new TextField(); tfCompteHisto.setPromptText("ID Compte");
        Button btnShowTrans = new Button("VOIR TRANSACTIONS");
        btnShowTrans.getStyleClass().add("btn-primary");
        searchRow.getChildren().addAll(tfCompteHisto, btnShowTrans);

        TableView<Transaction> table = new TableView<>();
        TableColumn<Transaction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("typeTransaction"));
        TableColumn<Transaction, Double> colMt = new TableColumn<>("Montant");
        colMt.setCellValueFactory(new PropertyValueFactory<>("montant"));
        TableColumn<Transaction, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateTransaction"));
        table.getColumns().addAll(colType, colMt, colDate);

        btnShowTrans.setOnAction(e -> {
            try {
                table.getItems().setAll(transactionDAO.getHistoriqueCompte(Integer.parseInt(tfCompteHisto.getText())));
            } catch (Exception ex) { showError("Erreur", "ID Compte invalide."); }
        });

        rightCol.getChildren().addAll(new Label("Historique Transactionnel"), searchRow, table);
        
        mainHBox.getChildren().addAll(leftCol, rightCol);
        container.getChildren().addAll(titre, mainHBox);
        return container;
    }

    // --- Utilitaires Alertes ---
    private void showAlert(String titre, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(message);
        a.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        a.showAndWait();
    }

    private void showError(String titre, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titre); a.setHeaderText(null); a.setContentText(message);
        a.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}