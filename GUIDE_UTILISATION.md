# Guide d'utilisation et d'explication du projet

## 1. Présentation

**Medical Supply Chain Management System** est une application de bureau Java à plusieurs pages pour gérer un petit inventaire de fournitures médicales. Elle suit les articles, les fournisseurs, les lots reçus, les dates d'expiration, les sorties vers les services et les commandes fournisseur.

L'application est conçue comme un prototype pédagogique autonome : elle ne nécessite ni serveur ni base de données externe. Les données sont conservées dans un fichier local.

## 2. Prérequis et lancement

Il faut disposer d'un JDK 17 ou supérieur.

### Avec Maven

Depuis la racine du projet, lancer :

```powershell
mvn compile exec:java
```

### Sans Maven

Dans PowerShell, depuis la racine du projet :

```powershell
$sources = Get-ChildItem -Recurse src\main\java -Filter *.java | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force target\classes | Out-Null
javac -encoding UTF-8 -d target\classes $sources
java -cp target\classes com.medical.supplychain.Main
```

Sous Windows, il est aussi possible de double-cliquer sur `lancer.bat` à la racine du projet. Ce fichier compile les sources et ouvre l'application graphique.

Au premier lancement, une fenêtre graphique s'ouvre avec des données de démonstration : quelques articles, un fournisseur et des lots de stock. Les données sont sauvegardées dans `data\medical-supply-chain.ser`.

Pour utiliser un autre fichier de sauvegarde, le passer en argument :

```powershell
java -cp target\classes com.medical.supplychain.Main "C:\medical-data\inventory.ser"
```

Conservez une copie de ce fichier si vous souhaitez sauvegarder ou transférer l'état de l'application. Ne supprimez pas le fichier pour quitter : les modifications sont enregistrées automatiquement après les opérations.

## 3. Navigation dans l'application

La barre latérale permet de naviguer entre cinq pages. Les actions de création, de réception et de sortie ouvrent des fenêtres de formulaire. Les sélecteurs affichent les noms et identifiants utiles ; les tableaux peuvent être triés en cliquant sur leurs en-têtes.

### Tableau de bord

Le tableau de bord présente le nombre d'articles et de fournisseurs, les commandes qui restent à recevoir, les articles sous leur seuil minimal et les lots arrivant à expiration dans les 30 jours.

Le seuil de stock bas est comparé au **stock utilisable** : les quantités déjà expirées ne comptent pas comme disponibles.

### Inventaire

L'inventaire affiche, pour chaque article, son identifiant, son nom, sa catégorie, son stock total, son stock utilisable et son seuil minimum.

- **Stock total** : quantité de tous les lots encore présents, y compris les lots expirés.
- **Stock utilisable** : quantité des lots dont la date d'expiration n'est pas passée.

La page rassemble les articles et les actions **Ajouter un article**, **Réceptionner du stock** et **Sortir du stock**. La réception et la sortie utilisent des formulaires avec sélecteurs d'articles et de fournisseurs.

### Ajouter un article

Dans la page **Inventaire**, cliquez sur **Ajouter un article** puis indiquez son nom, sa catégorie, son unité (par exemple « boîtes » ou « pièces ») et le seuil de stock minimum. Le programme génère son identifiant.

### Fournisseurs

Dans la page **Fournisseurs**, cliquez sur **Ajouter un fournisseur** et saisissez le nom, le contact et l'adresse e-mail. L'identifiant généré sera nécessaire lors de la réception d'un stock ou de la création d'une commande.

La page **Fournisseurs** liste les partenaires et propose le formulaire **Ajouter un fournisseur**.

### Réceptionner du stock

Sélectionnez un article et un fournisseur, puis indiquez la quantité reçue et sa date d'expiration au format `AAAA-MM-JJ`. Chaque réception crée un lot distinct, associé au fournisseur. Une date passée ou une quantité nulle ou négative est refusée.

### Sortir du stock (FEFO)

Sélectionnez l'article, la quantité et le service ou lieu destinataire (par exemple « Urgences », « Bloc opératoire » ou « Réserve centrale »).

La sortie applique la règle **FEFO** (*First Expired, First Out*) : les lots dont l'expiration est la plus proche sont utilisés en premier. Les lots expirés sont exclus. La sortie est refusée si le stock utilisable est insuffisant. Chaque sortie est ajoutée à l'historique avec sa date et sa destination.

### Commandes

La page liste les commandes, et propose **Créer une commande** : choisissez un fournisseur, un article et une quantité. La commande est créée avec le statut `ORDERED` (en attente de réception).

### Réceptionner une commande

Sélectionnez une commande en attente puis saisissez la date d'expiration du lot reçu. La quantité prévue est alors ajoutée au stock de l'article concerné et la commande passe au statut `RECEIVED`.

La version actuelle crée une commande pour **un article à la fois** et considère la réception de cette commande comme complète.

### Mouvements

La page **Mouvements** affiche l'historique des sorties : date, article, quantité et destination.

### Détails des commandes

La page **Commandes** affiche leur identifiant, leur date, leur fournisseur, leur statut et leurs lignes d'articles.

## 4. Organisation du projet

Le code source se trouve dans `src\main\java\com\medical\supplychain`.

| Fichier | Rôle |
|---|---|
| `Main.java` | Initialise les données et ouvre l'application graphique. |
| `DesktopApp.java` | Fenêtre Swing, navigation entre les pages, tableaux et formulaires. |
| `SupplyChainService.java` | Règles métier : validation, inventaire, sorties FEFO, alertes et réception des commandes. |
| `SupplyItem.java` | Article médical : nom, catégorie, unité, seuil minimal et liste de lots. |
| `StockLot.java` | Lot reçu : quantité, date d'expiration et fournisseur d'origine. |
| `Supplier.java` | Informations de contact du fournisseur. |
| `PurchaseOrder.java` | Commande fournisseur, lignes de commande et statut. |
| `StockMovement.java` | Historique d'une sortie de stock et de sa destination. |
| `AppData.java` | État complet de l'application : articles, fournisseurs, commandes et mouvements. |
| `DataStore.java` | Chargement et sauvegarde locale de l'état de l'application. |

### Parcours d'une opération

1. `DesktopApp` recueille les informations dans un formulaire.
2. `SupplyChainService` vérifie les données et applique les règles métier.
3. Les objets concernés sont mis à jour dans `AppData`.
4. `DataStore` sauvegarde l'état après l'action.

Les classes métier sont séparées de l'interface graphique pour que les règles puissent être réutilisées par une autre interface à l'avenir.

## 5. Structure du dépôt

```text
pom.xml
README.md
GUIDE_UTILISATION.md
src/
  main/
    java/
      com/medical/supplychain/
```

`pom.xml` décrit le projet Maven et fixe la version minimale de Java à 17. `target\` contient les classes compilées et `data\` contient les données locales générées à l'exécution ; ces dossiers ne sont pas destinés à être ajoutés au contrôle de version.

## 6. Limites de cette version

- Interface de bureau locale, sans authentification ni gestion des rôles.
- Sauvegarde locale à fichier, sans base de données partagée ni accès multi-utilisateur.
- Les commandes portent sur un seul article et sont réceptionnées en une fois.
- Les lots ne disposent pas de champs tels que le numéro de lot fabricant, le coût ou l'emplacement précis.
- L'application ne gère pas encore le transfert entre deux emplacements comme une opération distincte.

Ces limites sont à prendre en compte avant tout usage en environnement hospitalier réel.
