# Installation et démarrage de MediSupply

MediSupply est une application web Java : le navigateur ne lance pas directement les fichiers Java. Maven construit un fichier WAR, puis un conteneur Servlet démarre l'application.

## Outils nécessaires

1. **JDK 17 ou plus récent** — fournit `java` et `javac`. Le projet cible Java 17.
2. **Apache Maven 3.9 ou plus récent** — télécharge les dépendances et construit le WAR.
3. **Apache Tomcat 10.1 ou serveur Jakarta EE 10 compatible** — héberge le servlet Jakarta. Tomcat 10.1 implémente Servlet 6.0.
4. **Navigateur web** — pour ouvrir l'application à l'adresse locale.

Téléchargements officiels : [Eclipse Temurin JDK](https://adoptium.net/temurin/releases/), [Apache Maven](https://maven.apache.org/download.cgi), [Apache Tomcat 10](https://tomcat.apache.org/download-10.cgi).

## Installer et vérifier sur Windows

Installez le JDK et configurez `JAVA_HOME` vers son dossier, par exemple `C:\Program Files\Eclipse Adoptium\jdk-17...`. Ajoutez `%JAVA_HOME%\bin` à `PATH`. Installez Maven, puis ajoutez son dossier `bin` à `PATH`. Décompressez Tomcat 10.1 dans un emplacement accessible, par exemple `C:\servers\apache-tomcat-10.1`.

Ouvrez un nouveau PowerShell et vérifiez :

```powershell
java -version
javac -version
mvn -version
```

`java` et `javac` doivent afficher Java 17 ou plus récent.

## Construire et lancer

À la racine du projet, double-cliquez sur `lancer.bat` ou exécutez :

```powershell
.\lancer.bat
```

Le script construit `target/medical-supply-chain-1.0.0.war`, le copie dans Tomcat, démarre Tomcat si le port 8080 est libre et ouvre l'application dans le navigateur. Si Tomcat n'est pas dans `.tools\apache-tomcat-10.1.61`, définissez `CATALINA_HOME` vers votre installation avant de lancer le script.

Pour construire uniquement le WAR avec les commandes Maven :

```powershell
mvn -DskipTests package
```

Déployez ensuite le WAR dans Tomcat 10.1 et démarrez Tomcat avec `bin\startup.bat`. L'adresse par défaut est [http://localhost:8080/medical-supply-chain/](http://localhost:8080/medical-supply-chain/). Arrêtez Tomcat avec `bin\shutdown.bat`.

## Données

La configuration du projet enregistre les données dans `data/medical-supply-chain.ser`. Le paramètre de contexte `dataFile` de `src/main/webapp/WEB-INF/web.xml` permet de changer le chemin. Comme ce chemin relatif est résolu depuis le dossier de lancement du serveur, démarrez Tomcat depuis la racine du projet ou configurez un chemin absolu.

## Résolution rapide des problèmes

- `mvn` ou `java` introuvable : vérifiez `PATH`, `JAVA_HOME`, puis ouvrez un nouveau terminal.
- Le port 8080 est déjà utilisé : arrêtez l'autre serveur ou modifiez le port dans `conf/server.xml` de Tomcat.
- Erreur de déploiement : vérifiez que le serveur utilise Tomcat 10.1+ (namespace `jakarta.servlet`) et consultez `logs/` dans le dossier Tomcat.
