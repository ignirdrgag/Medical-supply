# Medical Supply Chain Management System

Application de bureau Java avec plusieurs pages pour suivre un inventaire de fournitures médicales, les lots et leurs dates d'expiration, les fournisseurs et les commandes.

Consultez le [guide d'utilisation et d'explication du projet](./GUIDE_UTILISATION.md) pour les instructions détaillées, la navigation entre les pages et l'architecture.

## Fonctionnalités

- Consultation de l'inventaire avec stock total, stock utilisable et seuil minimum.
- Création d'articles et de fournisseurs.
- Réception de stock par lot, avec traçabilité du fournisseur et date d'expiration.
- Sorties de stock appliquant FEFO (premier lot qui expire, premier sorti) et excluant les lots expirés.
- Historique des sorties par article et destination (service clinique ou lieu de stockage).
- Alertes sur les seuils bas et les lots expirant sous 30 jours.
- Création et réception de commandes fournisseur.
- Sauvegarde locale automatique dans `data/medical-supply-chain.ser`.
- Initialisation avec quelques données de démonstration au premier lancement.

## Prérequis

JDK 17 ou supérieur. Maven est facultatif pour compiler directement avec `javac`.

## Lancer avec Maven

```powershell
mvn compile exec:java
```

## Lancer avec le fichier de démarrage Windows

Double-cliquez sur `lancer.bat`, ou exécutez-le depuis PowerShell à la racine du projet :

```powershell
.\lancer.bat
```

Le fichier compile le projet puis ouvre l'application graphique. Pour choisir un autre fichier de sauvegarde :

```powershell
.\lancer.bat "C:\medical-data\inventory.ser"
```

## Compiler et lancer sans Maven (PowerShell)

```powershell
$sources = Get-ChildItem -Recurse src\main\java -Filter *.java | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force target\classes | Out-Null
javac -encoding UTF-8 -d target\classes $sources
java -cp target\classes com.medical.supplychain.Main
```

Un chemin de sauvegarde différent peut être passé en argument :

```powershell
java -cp target\classes com.medical.supplychain.Main "C:\medical-data\inventory.ser"
```
