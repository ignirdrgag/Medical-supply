package com.medical.supplychain;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Path dataFile = args.length > 0 ? Path.of(args[0]) : Path.of("data", "medical-supply-chain.ser");
        DataStore store = new DataStore(dataFile);
        try {
            AppData data = store.exists() ? store.load() : createDemoData();
            if (!store.exists()) {
                store.save(data);
            }
            SupplyChainService service = new SupplyChainService(data);
            DesktopApp.applyLookAndFeel();
            SwingUtilities.invokeLater(() -> new DesktopApp(service, store).setVisible(true));
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("Impossible de charger ou d'initialiser les données : "
                    + exception.getMessage());
            System.exit(1);
        }
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
