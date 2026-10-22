# MediSupply — application web JEE

Application web Java de gestion des fournitures médicales. L'interface HTTP est fournie par `SupplyChainWebServlet`; les règles métier restent dans `SupplyChainService` et les données sont enregistrées dans un fichier sérialisé.

## Outils requis

- JDK 17 ou supérieur
- Apache Maven 3.9 ou supérieur
- Un conteneur Jakarta Servlet 6, par exemple Tomcat 10.1+ ou WildFly compatible Jakarta EE 10
- Un navigateur web

Voir le [guide d'installation et de démarrage](./GUIDE_INSTALLATION.md) pour les téléchargements, la configuration de Windows et les étapes détaillées.

Pour une explication simple de chaque dossier, fichier et classe du projet, voir [EXPLICATION_SIMPLE.md](./EXPLICATION_SIMPLE.md).

## Construire et déployer

À la racine du projet :

```powershell
.\lancer.bat
```

Le script construit le WAR, le déploie dans le Tomcat local si `CATALINA_HOME` est défini, démarre le serveur et ouvre le navigateur. Pour construire seulement le WAR, lancez `mvn -DskipTests package`.

Le fichier `target/medical-supply-chain-1.0.0.war` est le livrable à déployer dans le répertoire `webapps` de Tomcat ou depuis la console de déploiement WildFly. Démarrez le serveur et ouvrez `http://localhost:8080/medical-supply-chain/` ou `http://localhost:8080/medical-supply-chain/inventory`.

Les données sont sauvegardées dans `data/medical-supply-chain.ser` pour cette configuration locale. Le chemin est configurable avec le paramètre de contexte `dataFile` dans `WEB-INF/web.xml`.

## Structure web

```text
src/main/java/com/medical/supplychain/   classes métier, service et servlet
src/main/webapp/WEB-INF/web.xml           descripteur de déploiement
pom.xml                                   dépendance Servlet fournie par le serveur
```

L'API Jakarta Servlet est déclarée avec la portée Maven `provided` : le conteneur fournit cette bibliothèque à l'exécution. Les règles FEFO et les validations restent assurées par `SupplyChainService`.
