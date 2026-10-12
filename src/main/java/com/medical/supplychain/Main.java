package com.medical.supplychain;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class Main {
    private static final Scanner INPUT = new Scanner(System.in);

    private Main() {
    }

    public static void main(String[] args) {
        Path dataFile = args.length > 0 ? Path.of(args[0]) : Path.of("data", "medical-supply-chain.ser");
        DataStore store = new DataStore(dataFile);
        try {
            AppData data = store.exists() ? store.load() : createDemoData();
            SupplyChainService service = new SupplyChainService(data);
            if (!store.exists()) {
                store.save(data);
            }
            run(service, store);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Impossible de charger ou d'initialiser les données : " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(SupplyChainService service, DataStore store) {
        System.out.println("=== Gestion de la chaîne d'approvisionnement médicale ===");
        System.out.println("Données sauvegardées localement.");
        boolean running = true;
        boolean dataSaved = true;
        while (running) {
            printMenu();
            String choice = prompt("Votre choix : ");
            try {
                switch (choice) {
                    case "1" -> showDashboard(service);
                    case "2" -> showInventory(service);
                    case "3" -> addItem(service);
                    case "4" -> addSupplier(service);
                    case "5" -> receiveStock(service);
                    case "6" -> issueStock(service);
                    case "7" -> createOrder(service);
                    case "8" -> receiveOrder(service);
                    case "9" -> showOrders(service);
                    case "10" -> showMovements(service);
                    case "0" -> running = false;
                    default -> System.out.println("Choix invalide.");
                }
                store.save(service.getData());
                dataSaved = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Erreur de sauvegarde : " + e.getMessage());
                dataSaved = false;
            }
        }
        System.out.println(dataSaved
                ? "Au revoir. Les données sont sauvegardées."
                : "Au revoir. Attention : la dernière sauvegarde a échoué.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Tableau de bord et alertes");
        System.out.println("2. Consulter le stock");
        System.out.println("3. Ajouter un article");
        System.out.println("4. Ajouter un fournisseur");
        System.out.println("5. Réceptionner du stock");
        System.out.println("6. Sortir du stock (FEFO)");
        System.out.println("7. Créer une commande fournisseur");
        System.out.println("8. Réceptionner une commande");
        System.out.println("9. Consulter les commandes");
        System.out.println("10. Historique des sorties");
        System.out.println("0. Quitter");
    }

    private static void showDashboard(SupplyChainService service) {
        System.out.println("\n--- Tableau de bord ---");
        System.out.println("Articles : " + service.getItems().size());
        System.out.println("Fournisseurs : " + service.getSuppliers().size());
        System.out.println("Commandes en attente : " + service.getOrders().stream()
                .filter(order -> order.getStatus() == PurchaseOrder.Status.ORDERED).count());

        System.out.println("\nAlertes de stock bas (stock utilisable <= seuil) :");
        if (service.getLowStockItems().isEmpty()) {
            System.out.println("Aucune alerte.");
        } else {
            service.getLowStockItems().forEach(item -> System.out.printf(" - %s : %d %s (seuil %d)%n",
                    item.getName(), service.getUsableQuantity(item), item.getUnit(), item.getMinimumStock()));
        }

        System.out.println("\nLots expirant dans les 30 jours :");
        boolean found = false;
        for (SupplyItem item : service.getItems()) {
            for (StockLot lot : service.getExpiringLots(item, 30)) {
                found = true;
                System.out.printf(" - %s : %d %s, expiration %s%n",
                        item.getName(), lot.getQuantity(), item.getUnit(), lot.getExpirationDate());
            }
        }
        if (!found) {
            System.out.println("Aucun lot à signaler.");
        }
    }

    private static void showInventory(SupplyChainService service) {
        System.out.println("\n--- Inventaire ---");
        if (service.getItems().isEmpty()) {
            System.out.println("Aucun article enregistré.");
            return;
        }
        System.out.printf("%-10s %-28s %-18s %10s %10s %10s%n",
                "ID", "Article", "Catégorie", "Stock", "Utilisable", "Seuil");
        for (SupplyItem item : service.getItems()) {
            System.out.printf("%-10s %-28s %-18s %10d %10d %10d%n",
                    item.getId(), item.getName(), item.getCategory(), service.getQuantity(item),
                    service.getUsableQuantity(item), item.getMinimumStock());
        }
    }

    private static void addItem(SupplyChainService service) {
        SupplyItem item = service.addItem(prompt("Nom : "), prompt("Catégorie : "),
                prompt("Unité (boîte, pièce, litre...) : "), readInt("Seuil minimum : "));
        System.out.println("Article ajouté. Identifiant : " + item.getId());
    }

    private static void addSupplier(SupplyChainService service) {
        Supplier supplier = service.addSupplier(prompt("Nom du fournisseur : "),
                prompt("Contact : "), prompt("E-mail : "));
        System.out.println("Fournisseur ajouté. Identifiant : " + supplier.getId());
    }

    private static void receiveStock(SupplyChainService service) {
        showInventory(service);
        showSuppliers(service);
        service.receiveStock(prompt("ID de l'article : "), readInt("Quantité reçue : "),
                readDate("Date d'expiration (AAAA-MM-JJ) : "), prompt("ID du fournisseur : "));
        System.out.println("Réception enregistrée.");
    }

    private static void issueStock(SupplyChainService service) {
        showInventory(service);
        service.issueStock(prompt("ID de l'article : "), readInt("Quantité à sortir : "),
                prompt("Service ou lieu de destination : "));
        System.out.println("Sortie enregistrée selon la règle FEFO.");
    }

    private static void createOrder(SupplyChainService service) {
        showSuppliers(service);
        showInventory(service);
        PurchaseOrder order = service.createOrder(prompt("ID du fournisseur : "),
                prompt("ID de l'article : "), readInt("Quantité à commander : "));
        System.out.println("Commande créée. Identifiant : " + order.getId());
    }

    private static void receiveOrder(SupplyChainService service) {
        showOrders(service);
        String orderId = prompt("ID de la commande à réceptionner : ");
        PurchaseOrder order = service.findOrderOptional(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + orderId));
        Map<String, LocalDate> expirationDates = new HashMap<>();
        for (PurchaseOrder.OrderLine line : order.getLines()) {
            System.out.printf("%s (%d unités)%n", service.getItemName(line.getItemId()), line.getQuantity());
            expirationDates.put(line.getItemId(), readDate("Date d'expiration (AAAA-MM-JJ) : "));
        }
        service.receiveOrder(orderId, expirationDates);
        System.out.println("Commande réceptionnée et stock mis à jour.");
    }

    private static void showOrders(SupplyChainService service) {
        System.out.println("\n--- Commandes ---");
        if (service.getOrders().isEmpty()) {
            System.out.println("Aucune commande enregistrée.");
            return;
        }
        for (PurchaseOrder order : service.getOrders()) {
            System.out.printf("%s | %s | %s | %s%n", order.getId(), order.getOrderDate(),
                    service.getSupplierName(order.getSupplierId()), order.getStatus());
            for (PurchaseOrder.OrderLine line : order.getLines()) {
                System.out.printf("    %s : %d%n", service.getItemName(line.getItemId()), line.getQuantity());
            }
        }
    }

    private static void showMovements(SupplyChainService service) {
        System.out.println("\n--- Historique des sorties ---");
        if (service.getMovements().isEmpty()) {
            System.out.println("Aucune sortie enregistrée.");
            return;
        }
        for (StockMovement movement : service.getMovements()) {
            System.out.printf("%s | %s | %s : %d | destination : %s%n",
                    movement.getDate(), movement.getId(), service.getItemName(movement.getItemId()),
                    movement.getQuantity(), movement.getDestination());
        }
    }

    private static void showSuppliers(SupplyChainService service) {
        System.out.println("\n--- Fournisseurs ---");
        if (service.getSuppliers().isEmpty()) {
            System.out.println("Aucun fournisseur enregistré.");
            return;
        }
        for (Supplier supplier : service.getSuppliers()) {
            System.out.printf("%s | %s | %s | %s%n",
                    supplier.getId(), supplier.getName(), supplier.getContact(), supplier.getEmail());
        }
    }

    private static int readInt(String message) {
        while (true) {
            String value = prompt(message);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre entier.");
            }
        }
    }

    private static LocalDate readDate(String message) {
        while (true) {
            String value = prompt(message);
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("Date invalide. Format attendu : AAAA-MM-JJ.");
            }
        }
    }

    private static String prompt(String message) {
        System.out.print(message);
        return INPUT.nextLine().trim();
    }

    private static AppData createDemoData() {
        AppData data = new AppData();
        SupplyChainService service = new SupplyChainService(data);
        Supplier supplier = service.addSupplier("MediSanté Distribution", "01 42 00 00 00",
                "commandes@medisante.example");
        SupplyItem gloves = service.addItem("Gants nitrile", "EPI", "boîtes", 20);
        SupplyItem masks = service.addItem("Masques chirurgicaux", "EPI", "boîtes", 15);
        SupplyItem reagent = service.addItem("Réactif de diagnostic", "Réactifs", "flacons", 10);
        service.receiveStock(gloves.getId(), 45, LocalDate.now().plusMonths(18), supplier.getId());
        service.receiveStock(masks.getId(), 12, LocalDate.now().plusMonths(8), supplier.getId());
        service.receiveStock(reagent.getId(), 8, LocalDate.now().plusDays(20), supplier.getId());
        return data;
    }

}
