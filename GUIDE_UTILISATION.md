# Guide d'utilisation et d'explication du projet

## 1. Présentation

**Medical Supply Chain Management System** est une application Java en ligne de commande pour gérer un petit inventaire de fournitures médicales. Elle suit les articles, les fournisseurs, les lots reçus, les dates d'expiration, les sorties vers les services et les commandes fournisseur.

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

Sous Windows, il est aussi possible de double-cliquer sur `lancer.bat` à la racine du projet. Ce fichier compile les sources et lance le menu.

Au premier lancement, l'application crée des données de démonstration : quelques articles, un fournisseur et des lots de stock. Les données sont sauvegardées dans `data\medical-supply-chain.ser`.

Pour utiliser un autre fichier de sauvegarde, le passer en argument :

```powershell
java -cp target\classes com.medical.supplychain.Main "C:\medical-data\inventory.ser"
```

Conservez une copie de ce fichier si vous souhaitez sauvegarder ou transférer l'état de l'application. Ne supprimez pas le fichier pour quitter : les modifications sont enregistrées automatiquement après les opérations.

## 3. Utilisation du menu

Le programme affiche un menu numéroté. Saisissez le numéro de l'action souhaitée puis répondez aux questions affichées. Les identifiants affichés dans les listes servent à sélectionner les articles, fournisseurs et commandes.

### Tableau de bord et alertes

Le tableau de bord présente le nombre d'articles et de fournisseurs, les commandes qui restent à recevoir, les articles sous leur seuil minimal et les lots arrivant à expiration dans les 30 jours.

Le seuil de stock bas est comparé au **stock utilisable** : les quantités déjà expirées ne comptent pas comme disponibles.

### Consulter le stock

L'inventaire affiche, pour chaque article, son identifiant, son nom, sa catégorie, son stock total, son stock utilisable et son seuil minimum.

- **Stock total** : quantité de tous les lots encore présents, y compris les lots expirés.
- **Stock utilisable** : quantité des lots dont la date d'expiration n'est pas passée.

### Ajouter un article

Saisissez son nom, sa catégorie, son unité (par exemple « boîtes » ou « pièces ») et le seuil de stock minimum. Le programme génère son identifiant.

### Ajouter un fournisseur

Saisissez le nom, le contact et l'adresse e-mail. L'identifiant généré sera nécessaire lors de la réception d'un stock ou de la création d'une commande.

### Réceptionner du stock

Sélectionnez un article et un fournisseur, puis indiquez la quantité reçue et sa date d'expiration au format `AAAA-MM-JJ`. Chaque réception crée un lot distinct, associé au fournisseur. Une date passée ou une quantité nulle ou négative est refusée.

### Sortir du stock (FEFO)

Sélectionnez l'article, la quantité et le service ou lieu destinataire (par exemple « Urgences », « Bloc opératoire » ou « Réserve centrale »).

La sortie applique la règle **FEFO** (*First Expired, First Out*) : les lots dont l'expiration est la plus proche sont utilisés en premier. Les lots expirés sont exclus. La sortie est refusée si le stock utilisable est insuffisant. Chaque sortie est ajoutée à l'historique avec sa date et sa destination.

### Créer une commande fournisseur

Choisissez un fournisseur, un article et une quantité. La commande est créée avec le statut `ORDERED` (en attente de réception).

### Réceptionner une commande

Sélectionnez une commande en attente puis saisissez la date d'expiration du lot reçu. La quantité prévue est alors ajoutée au stock de l'article concerné et la commande passe au statut `RECEIVED`.

La version actuelle crée une commande pour **un article à la fois** et considère la réception de cette commande comme complète.

### Consulter les commandes et l'historique

La liste des commandes affiche leur identifiant, leur date, leur fournisseur, leur statut et leurs lignes d'articles. L'historique des sorties permet de retrouver l'article, la quantité, la date et la destination.

## 4. Organisation du projet

Le code source se trouve dans `src\main\java\com\medical\supplychain`.

| Fichier | Rôle |
|---|---|
| `Main.java` | Point d'entrée, menu interactif, lecture des saisies et affichage des résultats. |
| `SupplyChainService.java` | Règles métier : validation, inventaire, sorties FEFO, alertes et réception des commandes. |
| `SupplyItem.java` | Article médical : nom, catégorie, unité, seuil minimal et liste de lots. |
| `StockLot.java` | Lot reçu : quantité, date d'expiration et fournisseur d'origine. |
| `Supplier.java` | Informations de contact du fournisseur. |
| `PurchaseOrder.java` | Commande fournisseur, lignes de commande et statut. |
| `StockMovement.java` | Historique d'une sortie de stock et de sa destination. |
| `AppData.java` | État complet de l'application : articles, fournisseurs, commandes et mouvements. |
| `DataStore.java` | Chargement et sauvegarde locale de l'état de l'application. |

### Parcours d'une opération

1. `Main` recueille les informations auprès de l'utilisateur.
2. `SupplyChainService` vérifie les données et applique les règles métier.
3. Les objets concernés sont mis à jour dans `AppData`.
4. `DataStore` sauvegarde l'état après l'action.

Les classes métier sont séparées de l'interface console pour que les règles puissent être réutilisées par une autre interface à l'avenir.

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

- Interface en ligne de commande, sans authentification ni gestion des rôles.
- Sauvegarde locale à fichier, sans base de données partagée ni accès multi-utilisateur.
- Les commandes portent sur un seul article et sont réceptionnées en une fois.
- Les lots ne disposent pas de champs tels que le numéro de lot fabricant, le coût ou l'emplacement précis.
- L'application ne gère pas encore le transfert entre deux emplacements comme une opération distincte.

Ces limites sont à prendre en compte avant tout usage en environnement hospitalier réel.
