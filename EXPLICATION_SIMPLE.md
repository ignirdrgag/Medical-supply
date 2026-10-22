# Explication simple du projet MediSupply

Ce document explique **tout le projet, dossier par dossier et fichier par fichier**, avec des mots simples.
Il est fait pour quelqu'un qui découvre le code.

---

## 1. Ce que fait l'application en une phrase

MediSupply est une petite application Java qui gère un **inventaire de fournitures médicales** :
des articles, des fournisseurs, des lots avec date d'expiration, des commandes et des sorties de stock.

Elle peut être utilisée de deux façons :

- **Version web** (celle qui est déployée aujourd'hui) : on ouvre une page dans le navigateur.
- **Version bureau** (ancienne version) : une fenêtre Java classique avec des boutons.

Les deux versions utilisent **le même code métier** et **le même fichier de sauvegarde**.

---

## 2. L'arborescence complète

```text
projet_java/
├── .git/                       historique Git du projet (ne pas modifier)
├── .gitignore                  liste des fichiers que Git doit ignorer
├── .tools/                     Maven et Tomcat installés localement (ne pas modifier)
├── data/
│   └── medical-supply-chain.ser fichier de sauvegarde des données
├── src/
│   └── main/
│       ├── java/
│       │   └── com/medical/supplychain/
│       │       ├── AppData.java
│       │       ├── DataStore.java
│       │       ├── DesktopApp.java
│       │       ├── Main.java
│       │       ├── PurchaseOrder.java
│       │       ├── StockLot.java
│       │       ├── StockMovement.java
│       │       ├── Supplier.java
│       │       ├── SupplyChainService.java
│       │       ├── SupplyChainWebServlet.java
│       │       └── SupplyItem.java
│       └── webapp/
│           └── WEB-INF/
│               └── web.xml
├── target/                     résultat de la compilation (généré, ne pas modifier)
├── CODE_GUIDE_EN.md            guide du code en anglais
├── EXPLICATION_SIMPLE.md       ce document
├── GUIDE_CODE.md               guide technique du code
├── GUIDE_INSTALLATION.md       comment installer et lancer
├── GUIDE_UTILISATION.md        comment utiliser l'application
├── README.md                   présentation rapide du projet
├── lancer.bat                  script Windows pour compiler, déployer et ouvrir
└── pom.xml                     configuration Maven (dépendances et build)
```

---

## 3. Les dossiers, un par un

### `.git/`
Dossier caché créé par Git. Il contient l'historique des versions du projet.
**On n'y touche jamais à la main.**

### `.gitignore`
Ce n'est pas un dossier mais un fichier de configuration. Il dit à Git de ne pas sauvegarder :

- `target/` : résultat de compilation
- `.tools/` : Maven et Tomcat locaux
- `data/`, `*.ser`, `*.ser.tmp` : données personnelles et fichiers temporaires

Grâce à lui, on ne met pas dans Git des fichiers lourds ou propres à ta machine.

### `.tools/`
Contient les outils portables utilisés par `lancer.bat` :

- Apache Maven (pour compiler et fabriquer le fichier `.war`)
- Apache Tomcat (le serveur web qui exécute le servlet)
- `.m2repo` (copie locale des bibliothèques téléchargées)

C'est un dossier technique, généré pour le projet. **On ne modifie rien dedans.**

### `data/`
Contient la sauvegarde de l'application :

- `medical-supply-chain.ser` : le fichier où sont enregistrés les articles, fournisseurs, commandes, etc.

C'est un fichier **binaire** (illisible dans un éditeur de texte). Si on le supprime, l'application repart de zéro (ou recrée des données de démonstration en mode bureau).

### `src/`
Contient tout le **code source**. `src` signifie « source ».

### `src/main/`
Partie principale du code (par opposition à `src/test`, qui contiendrait les tests ; il n'y en a pas ici).

### `src/main/java/`
Contient les fichiers Java.

### `src/main/java/com/medical/supplychain/`
C'est le **package** (le « dossier logique ») de l'application.
Toutes les classes Java sont ici et commencent par la ligne `package com.medical.supplychain;`.

### `src/main/webapp/`
Contient les fichiers propres à une application web.

### `src/main/webapp/WEB-INF/`
Dossier spécial d'une application web Java : son contenu n'est **pas accessible directement** depuis le navigateur pour des raisons de sécurité. C'est là qu'on met la configuration.

### `target/`
Créé automatiquement par Maven lors de la compilation. Il contient :

- les `.class` (code compilé)
- le fichier final `medical-supply-chain-1.0.0.war`

**On ne modifie jamais `target/` à la main** : il est recréé à chaque build.

---

## 4. Les fichiers de la racine, un par un

### `README.md`
La **porte d'entrée** du projet : à quoi il sert, quels outils il faut, comment le lancer.
Quand quelqu'un découvre le projet, c'est le premier fichier à lire.

### `pom.xml`
Le **chef d'orchestre de Maven**. Il déclare :

- l'identité du projet : `com.medical` / `medical-supply-chain` / `1.0.0`
- le type de livrable : `war` (une application web Java)
- la version de Java : 17
- la seule dépendance : `jakarta.servlet-api` en version 6.0.0, avec la portée `provided`

`provided` veut dire : « le serveur Tomcat fournit déjà cette bibliothèque, donc ne l'embarque pas dans le fichier final ».
Il déclare aussi les plugins qui compilent le code et fabriquent le `.war`.

### `lancer.bat`
Script Windows qui fait tout automatiquement :

1. cherche Maven (dans `.tools` puis dans le `PATH`)
2. compile le projet avec `mvn package`
3. cherche Tomcat (variable `CATALINA_HOME` ou `.tools`)
4. copie le `.war` dans le dossier `webapps` de Tomcat
5. démarre Tomcat s'il n'est pas déjà lancé
6. attend que l'application réponde
7. ouvre le navigateur sur `http://localhost:8080/medical-supply-chain/`

C'est le fichier à double-cliquer pour utiliser l'application web.

### `.gitignore`
Déjà expliqué plus haut : il évite de versionner les fichiers inutiles ou personnels.

### `CODE_GUIDE_EN.md`
Version anglaise du guide technique du code. Utile pour un lecteur anglophone.

### `GUIDE_CODE.md`
Guide **technique** du code : architecture, rôle des classes, structure Maven, déploiement.
Un peu plus court et plus « développeur » que le présent document.

### `GUIDE_INSTALLATION.md`
Guide pas à pas pour installer Java, Maven et Tomcat, puis lancer le projet sur Windows.

### `GUIDE_UTILISATION.md`
Guide d'utilisation : fonctionnalités et mode d'emploi de l'application (surtout la version bureau).

### `EXPLICATION_SIMPLE.md`
Le présent document : explication **simple et complète** de chaque dossier, fichier et classe.

---

## 5. Les fichiers de configuration

### `src/main/webapp/WEB-INF/web.xml`
Le **descripteur de déploiement** de l'application web. Il contient :

- le nom affiché de l'application : `MediSupply`
- un paramètre de contexte `dataFile` qui vaut `data/medical-supply-chain.ser`

Ce paramètre indique au servlet **où enregistrer les données**.
Le servlet le lit avec `getServletContext().getInitParameter("dataFile")`.

---

## 6. Les classes Java, une par une

Toutes les classes sont dans `src/main/java/com/medical/supplychain/`.

### `Main.java`
**Rôle :** point de départ de la **version bureau** (Swing).

- `main` prépare le fichier de données, charge les données et ouvre la fenêtre `DesktopApp`.
- Si le fichier n'existe pas, `createDemoData()` crée quelques articles, un fournisseur et des lots d'exemple.
- Le chemin du fichier peut être passé en argument de la commande.

Cette classe n'est **pas utilisée** quand on passe par le navigateur : la version web démarre par le servlet.

### `DesktopApp.java`
**Rôle :** toute l'**interface graphique** de la version bureau.

- C'est une fenêtre (`JFrame`) avec un menu à gauche et 5 pages :
  - `dashboard` : tableau de bord, alertes de stock bas et lots proches de l'expiration
  - `inventory` : inventaire et boutons d'action
  - `suppliers` : fournisseurs
  - `orders` : commandes fournisseur
  - `movements` : historique des sorties
- `createDashboardPage()`, `createInventoryPage()`, etc. construisent chaque page.
- `addItem()`, `addSupplier()`, `receiveStock()`, `issueStock()`, `createOrder()`, `receiveOrder()` affichent des formulaires et appellent le service.
- `execute()` exécute une action, sauvegarde les données, puis rafraîchit l'affichage.

C'est la plus grosse classe du projet (environ 790 lignes), parce que tout le dessin de l'interface est fait à la main.

### `SupplyChainWebServlet.java`
**Rôle :** point d'entrée de la **version web**. C'est le « contrôleur ».

- `@WebServlet(urlPatterns = {"/", "/inventory"})` : répond aux adresses `/` et `/inventory`.
- `init()` : au démarrage, ouvre le fichier de données et crée le service. Le chemin vient de `web.xml`.
- `doGet()` : construit la page HTML (inventaire + formulaires).
- `doPost()` : reçoit les formulaires et lance l'action demandée :
  - `item` : ajouter un article
  - `supplier` : ajouter un fournisseur
  - `receive` : réceptionner un lot
  - `issue` : sortir du stock
- Après l'action, il sauvegarde puis redirige vers `/inventory?message=...`.
- `escape()` : transforme les caractères spéciaux (`<`, `>`, `&`...) en texte inoffensif pour éviter les injections HTML (XSS).

### `SupplyChainService.java`
**Rôle :** le **cerveau métier**. Il contient les règles et les vérifications.

C'est cette classe qu'il faut lire pour comprendre le fonctionnement réel :

- `addItem()` / `addSupplier()` : ajoutent un article ou un fournisseur après vérification des champs obligatoires.
- `getQuantity()` : additionne les quantités de tous les lots d'un article.
- `getUsableQuantity()` : additionne seulement les lots **non périmés**.
- `receiveStock()` : crée un nouveau lot, en vérifiant la quantité et la date d'expiration.
- `issueStock()` : sort une quantité du stock en appliquant la règle **FEFO** (voir plus bas).
- `createOrder()` : crée une commande fournisseur.
- `receiveOrder()` : transforme une commande en lots de stock.
- `getLowStockItems()` : liste les articles sous leur seuil minimum.
- `getExpiringLots()` : liste les lots qui expirent bientôt.
- Les méthodes `findItem`, `findSupplier`, `findOrder` retrouvent un objet par son identifiant ou lèvent une erreur s'il n'existe pas.

### `AppData.java`
**Rôle :** le **conteneur global** de toutes les données.

Il contient quatre listes :

- `items` : les articles
- `suppliers` : les fournisseurs
- `orders` : les commandes
- `movements` : les mouvements de stock (sorties)

C'est cet objet unique qui est sauvegardé dans le fichier `.ser`.

### `SupplyItem.java`
**Rôle :** représenter un **article** (modèle).

Champs :

- `id` : identifiant unique généré automatiquement (`UUID`, 8 caractères)
- `name` : nom de l'article
- `category` : catégorie (EPI, Réactifs...)
- `unit` : unité (boîtes, flacons...)
- `minimumStock` : seuil d'alerte
- `lots` : liste des lots de cet article

Un article ne stocke pas directement une quantité : il stocke des **lots**, et la quantité totale est la somme des lots.

### `StockLot.java`
**Rôle :** représenter un **lot** de stock.

Champs :

- `id` : identifiant unique
- `quantity` : quantité restante dans ce lot
- `expirationDate` : date de péremption
- `supplierId` : fournisseur d'origine

`removeQuantity()` réduit la quantité du lot et refuse une quantité invalide.
C'est le lot qui porte la date d'expiration, ce qui permet de sortir en priorité ce qui périme le plus tôt.

### `Supplier.java`
**Rôle :** représenter un **fournisseur**.

Champs : `id`, `name`, `contact`, `email`.
Il y a seulement le constructeur et les getters : c'est un objet simple de données.

### `PurchaseOrder.java`
**Rôle :** représenter une **commande fournisseur**.

- `id` : identifiant unique
- `supplierId` : fournisseur concerné
- `orderDate` : date de la commande
- `lines` : lignes de commande (`OrderLine` = un article + une quantité)
- `status` : `ORDERED` (commandée) ou `RECEIVED` (reçue)
- `markReceived()` : passe le statut à « reçue »

La classe interne `OrderLine` représente une ligne : un `itemId` et une `quantity`.

### `StockMovement.java`
**Rôle :** garder une **trace d'une sortie de stock**.

Champs : `id`, `itemId`, `quantity`, `destination`, `date`.
On crée un mouvement à chaque `issueStock()`. Cela permet d'afficher l'historique des sorties dans la page « Mouvements ».

### `DataStore.java`
**Rôle :** **sauvegarder et recharger** les données dans le fichier `.ser`.

- `exists()` : le fichier existe-t-il déjà ?
- `load()` : lit le fichier et reconstruit un objet `AppData`.
- `save()` : écrit les données dans un fichier temporaire, puis le renomme en fichier final.

L'écriture passe par un fichier temporaire (`.tmp`) pour éviter de perdre toutes les données si l'écriture est interrompue.
La technique utilisée s'appelle la **sérialisation Java** : on transforme un objet Java en octets, puis on le relit.

---

## 7. Comment les fichiers travaillent ensemble

### Quand on ouvre la page web

```text
Navigateur
   -> GET /inventory
   -> SupplyChainWebServlet.doGet()
   -> SupplyChainService (getItems, getQuantity, ...)
   -> AppData (les listes de données)
   -> le servlet écrit du HTML
   -> le navigateur affiche la page
```

### Quand on ajoute un article

```text
Formulaire HTML
   -> POST avec action=item
   -> SupplyChainWebServlet.doPost()
   -> SupplyChainService.addItem()  (vérifications + création)
   -> AppData.getItems().add(...)
   -> DataStore.save()             (écriture dans le fichier .ser)
   -> redirection vers /inventory?message=Opération enregistrée
```

### Quand on reçoit un lot

```text
doPost action=receive
   -> SupplyChainService.receiveStock()
   -> vérifie quantité > 0 et date valide
   -> crée un StockLot et l'ajoute aux lots de l'article
   -> DataStore.save()
```

### Quand on sort du stock

```text
doPost action=issue
   -> SupplyChainService.issueStock()
   -> vérifie le stock utilisable
   -> trie les lots du plus proche au plus lointain de l'expiration (FEFO)
   -> retire la quantité des lots les plus urgents
   -> crée un StockMovement pour l'historique
   -> DataStore.save()
```

### FEFO, c'est quoi ?

**FEFO** = *First Expired, First Out* : « le premier périmé sort le premier ».
Quand on retire du stock, on prend d'abord dans le lot dont la date d'expiration est la plus proche, pour limiter les pertes.

---

## 8. Les notions techniques expliquées simplement

| Notion | Explication simple |
|---|---|
| **Classe** | Un modèle ou un plan de construction (ex. `Supplier` décrit ce qu'est un fournisseur). |
| **Objet** | Une chose concrète fabriquée à partir d'une classe (ex. le fournisseur « MediSanté »). |
| **Package** | Un dossier logique qui regroupe des classes liées (`com.medical.supplychain`). |
| **Servlet** | Une classe Java exécutée par un serveur web pour répondre à une requête HTTP. |
| **WAR** | Une archive `.war` contenant l'application web prête à déployer dans Tomcat. |
| **Maven** | Un outil qui compile le code, gère les bibliothèques et fabrique le `.war`. |
| **Tomcat** | Le serveur qui exécute le servlet et affiche l'application dans le navigateur. |
| **Sérialisation** | Transformer un objet Java en fichier binaire, puis savoir le relire. |
| **UUID** | Un identifiant quasi unique généré automatiquement. |
| **`LocalDate`** | Une date sans heure (ex. `2026-10-22`). |
| **Exception** | Une erreur signalée par le code (ex. quantité négative). |
| **`synchronized`** | Empêche deux requêtes web de modifier les données en même temps. |
| **Stream** | Une façon moderne de filtrer/trier/additionner une liste en Java. |
| **`escape()`** | Rend le texte saisi inoffensif avant de l'afficher dans du HTML. |

---

## 9. Dans quel ordre lire le code

Si tu veux comprendre le projet progressivement :

1. `README.md` : le contexte
2. `pom.xml` : comment le projet est construit
3. `AppData.java` : quelles données existent
4. `SupplyItem.java`, `StockLot.java`, `Supplier.java` : les objets de base
5. `SupplyChainService.java` : les règles métier
6. `DataStore.java` : la sauvegarde
7. `SupplyChainWebServlet.java` : le lien avec le navigateur
8. `PurchaseOrder.java`, `StockMovement.java` : les objets complémentaires
9. `DesktopApp.java` et `Main.java` : l'ancienne interface bureau

---

## 10. Résumé : un fichier = une phrase

| Fichier | En une phrase |
|---|---|
| `Main.java` | Lance la version bureau avec des données de démonstration. |
| `DesktopApp.java` | Dessine la fenêtre Swing et ses 5 pages. |
| `SupplyChainWebServlet.java` | Reçoit les requêtes web et affiche les formulaires. |
| `SupplyChainService.java` | Applique les règles métier et les validations. |
| `AppData.java` | Regroupe toutes les listes de données. |
| `SupplyItem.java` | Décrit un article et ses lots. |
| `StockLot.java` | Décrit un lot et sa date d'expiration. |
| `Supplier.java` | Décrit un fournisseur. |
| `PurchaseOrder.java` | Décrit une commande et son statut. |
| `StockMovement.java` | Décrit une sortie de stock. |
| `DataStore.java` | Sauvegarde et recharge les données. |
| `web.xml` | Configure l'application web et le chemin des données. |
| `pom.xml` | Configure Maven et les dépendances. |
| `lancer.bat` | Compile, déploie, démarre et ouvre l'application. |
| `.gitignore` | Indique à Git les fichiers à ignorer. |
| `target/` | Contient le résultat de la compilation. |
| `data/` | Contient la sauvegarde des données. |
| `.tools/` | Contient Maven et Tomcat locaux. |
