# Gestion Banque JavaFX (Maven)

Application de gestion bancaire (Clients, Comptes, Opérations, Historique) avec JavaFX, MySQL, DAO/Service.

## Prérequis
- JDK 17 installé
- Maven 3.8+ installé
- MySQL 8+ en local (service démarré)

## Installation de la base de données
1. Ouvrir un client MySQL (mysql CLI, Workbench, DBeaver, etc.).
2. Exécuter le script SQL:
   - Fichier: `src/main/resources/db/schema.sql`
3. Vérifier que la base `gestion_banque` et les tables ont été créées.

## Configuration connexion MySQL
Éditer `src/main/java/com/banque/config/DatabaseConnection.java` et mettre vos identifiants:
```java
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe"; // <- modifiez ici
```
Le JDBC URL par défaut: `jdbc:mysql://localhost:3306/gestion_banque?useSSL=false&serverTimezone=UTC`

## Import dans Eclipse
1. File > Import... > Maven > Existing Maven Projects
2. Root Directory: `.../gestion-banque-javafx`
3. Finish
4. Clic droit sur le projet > Maven > Update Project (pour télécharger les deps)

## Lancer l'application
- Via Maven (terminal, dans le dossier du projet):
```bash
mvn clean javafx:run
```
- Via Eclipse:
  - Clic droit sur le projet > Run As > Maven build...
  - Goals: `javafx:run`
  - Run

L'écran affiche 4 onglets: `Clients`, `Comptes`, `Opérations`, `Historique`.

## Remarques
- Les opérations de virement sont transactionnelles via `com.banque.service.BanqueService`.
- Les dépôts/retraits et l'historique utilisent `com.banque.dao.*`.
- Si JavaFX ne s'affiche pas sous Linux, assurez-vous d'avoir un environnement graphique disponible.
