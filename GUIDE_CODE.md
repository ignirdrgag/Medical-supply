# Guide du code MediSupply (version web JEE)

Le projet est maintenant une application Maven `war`, destinée à un conteneur Jakarta Servlet 6 (Tomcat 10.1+ ou serveur Jakarta EE 10 compatible).

## Architecture

```text
Navigateur → SupplyChainWebServlet → SupplyChainService → AppData / modèles
                                              ↓
                                         DataStore
```

- `SupplyChainWebServlet` gère les requêtes HTTP et les formulaires web. Il affiche l'inventaire et traite l'ajout d'articles/fournisseurs, la réception de lots et les sorties de stock. Il est déclaré par `@WebServlet` sur `/` et `/inventory`.
- `SupplyChainService` conserve les règles métier et validations, notamment la sortie FEFO.
- `AppData` regroupe les articles, fournisseurs, commandes et mouvements.
- `DataStore` charge et sauvegarde l'état dans un fichier sérialisé.
- `SupplyItem`, `Supplier`, `StockLot`, `PurchaseOrder` et `StockMovement` sont les objets métier persistés.
- `DesktopApp` et `Main` sont les anciens points d'entrée Swing; le déploiement web passe par le servlet et ne les utilise pas.

## Structure Maven Web

```text
pom.xml
src/main/java/com/medical/supplychain/
src/main/webapp/WEB-INF/web.xml
src/test/java/
```

Maven produit `target/medical-supply-chain-1.0.0.war`. L'API `jakarta.servlet-api` est en portée `provided`, car elle est fournie par le serveur d'application.

## Déploiement local

Construisez avec `mvn clean package`, déployez le WAR sur Tomcat 10.1+ ou WildFly compatible Jakarta EE 10, puis ouvrez `/medical-supply-chain/` sur le serveur (port 8080 par défaut). Le fichier de données de cette configuration locale est `data/medical-supply-chain.ser`; le paramètre `dataFile` permet d'indiquer un autre chemin.
