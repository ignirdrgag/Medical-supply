package com.medical.supplychain;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class DesktopApp extends JFrame {
    private static final Color FOREST_GREEN = new Color(35, 91, 61);
    private static final Color FOREST_PALE = new Color(229, 240, 233);
    private static final Color WHITE = Color.WHITE;
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color TEXT = Color.BLACK;
    private static final Color MUTED = Color.BLACK;
    private static final Color BORDER = new Color(216, 229, 219);

    private final SupplyChainService service;
    private final DataStore store;
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pages = new JPanel(pageLayout);
    private final JLabel pageTitle = new JLabel();
    private final JLabel pageSubtitle = new JLabel();
    private final Map<String, JPanel> pagePanels = new HashMap<>();
    private final Map<String, JButton> navigationButtons = new HashMap<>();

    public DesktopApp(SupplyChainService service, DataStore store) {
        this.service = service;
        this.store = store;
        setTitle("MediSupply - Gestion de la chaîne médicale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 660));
        setSize(1360, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        add(createSidebar(), BorderLayout.WEST);
        add(createWorkspace(), BorderLayout.CENTER);
        refreshPages();
        showPage("dashboard", "Tableau de bord");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(WHITE);
        sidebar.setPreferredSize(new Dimension(248, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 20, 15));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(2, 5, 36, 4));
        JLabel brandMark = new JLabel("+");
        brandMark.setHorizontalAlignment(SwingConstants.CENTER);
        brandMark.setForeground(FOREST_GREEN);
        brandMark.setFont(new Font("SansSerif", Font.BOLD, 26));
        brandMark.setOpaque(true);
        brandMark.setBackground(FOREST_PALE);
        brandMark.setBorder(BorderFactory.createLineBorder(FOREST_GREEN));
        brandMark.setPreferredSize(new Dimension(42, 42));
        JLabel logo = new JLabel("<html><b>MediSupply</b><br><font size='3' color='#000000'>GESTION MÉDICALE</font></html>");
        logo.setForeground(Color.BLACK);
        logo.setFont(new Font("SansSerif", Font.PLAIN, 17));
        brand.add(brandMark, BorderLayout.WEST);
        brand.add(logo, BorderLayout.CENTER);
        sidebar.add(brand);

        JLabel sectionLabel = new JLabel("ESPACE DE TRAVAIL");
        sectionLabel.setForeground(Color.BLACK);
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 11, 11, 0));
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sectionLabel);

        addNavigationButton(sidebar, "01", "Tableau de bord", "dashboard");
        addNavigationButton(sidebar, "02", "Inventaire", "inventory");
        addNavigationButton(sidebar, "03", "Fournisseurs", "suppliers");
        addNavigationButton(sidebar, "04", "Commandes", "orders");
        addNavigationButton(sidebar, "05", "Mouvements", "movements");
        sidebar.add(Box.createVerticalGlue());

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(15, 7, 0, 0)));
        JLabel footerTitle = new JLabel("ENVIRONNEMENT LOCAL");
        footerTitle.setForeground(Color.BLACK);
        footerTitle.setFont(new Font("SansSerif", Font.BOLD, 9));
        JLabel footerText = new JLabel("Données sauvegardées");
        footerText.setForeground(Color.BLACK);
        footerText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.add(footerTitle);
        footer.add(Box.createVerticalStrut(6));
        footer.add(footerText);
        sidebar.add(footer);
        return sidebar;
    }

    private void addNavigationButton(JPanel sidebar, String number, String label, String page) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(13, 0));
        JLabel icon = new JLabel(number);
        icon.setForeground(FOREST_GREEN);
        icon.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel text = new JLabel(label);
        text.setForeground(Color.BLACK);
        text.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.add(icon, BorderLayout.WEST);
        button.add(text, BorderLayout.CENTER);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(0, 13, 0, 12));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setPreferredSize(new Dimension(218, 48));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.addActionListener(event -> showPage(page, label));
        navigationButtons.put(page, button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(0, 25));
        workspace.setBackground(BACKGROUND);
        workspace.setBorder(BorderFactory.createEmptyBorder(31, 36, 30, 36));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 29));
        pageTitle.setForeground(TEXT);
        pageSubtitle.setForeground(MUTED);
        pageSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(pageTitle);
        titles.add(Box.createVerticalStrut(7));
        titles.add(pageSubtitle);
        header.add(titles, BorderLayout.WEST);
        JLabel status = new JLabel("  ●  SYSTÈME OPÉRATIONNEL  ");
        status.setOpaque(true);
        status.setBackground(FOREST_PALE);
        status.setForeground(Color.BLACK);
        status.setFont(new Font("SansSerif", Font.BOLD, 10));
        status.setBorder(BorderFactory.createEmptyBorder(9, 8, 9, 8));
        header.add(status, BorderLayout.EAST);
        workspace.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        pages.setOpaque(false);
        content.add(pages, BorderLayout.CENTER);
        workspace.add(content, BorderLayout.CENTER);
        return workspace;
    }

    private void showPage(String key, String title) {
        pageTitle.setText(title);
        pageSubtitle.setText(switch (key) {
            case "dashboard" -> "Vue d’ensemble de vos stocks et de vos alertes.";
            case "inventory" -> "Consultez les niveaux et gérez les entrées et sorties.";
            case "suppliers" -> "Gérez les partenaires de votre chaîne d’approvisionnement.";
            case "orders" -> "Suivez les commandes, de leur création à leur réception.";
            case "movements" -> "Consultez les sorties de stock et leurs destinations.";
            default -> "";
        });
        navigationButtons.forEach((page, button) -> {
            boolean selected = page.equals(key);
            button.setBackground(selected ? FOREST_PALE : WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, selected ? 3 : 3, 0, 0,
                            selected ? FOREST_GREEN : WHITE),
                    BorderFactory.createEmptyBorder(0, 13, 0, 12)));
            JLabel number = (JLabel) button.getComponent(0);
            number.setForeground(FOREST_GREEN);
        });
        pageLayout.show(pages, key);
    }

    private void refreshPages() {
        pages.removeAll();
        pagePanels.clear();
        addPage("dashboard", createDashboardPage());
        addPage("inventory", createInventoryPage());
        addPage("suppliers", createSuppliersPage());
        addPage("orders", createOrdersPage());
        addPage("movements", createMovementsPage());
        pages.revalidate();
        pages.repaint();
    }

    private void addPage(String key, JPanel panel) {
        pagePanels.put(key, panel);
        pages.add(panel, key);
    }

    private JPanel createDashboardPage() {
        JPanel panel = pageContainer();
        int pendingOrders = (int) service.getOrders().stream()
                .filter(order -> order.getStatus() == PurchaseOrder.Status.ORDERED).count();
        JPanel metrics = new JPanel(new java.awt.GridLayout(1, 3, 14, 0));
        metrics.setOpaque(false);
        metrics.setPreferredSize(new Dimension(0, 132));
        metrics.add(metricCard("Articles suivis", String.valueOf(service.getItems().size()), "Références en catalogue"));
        metrics.add(metricCard("Fournisseurs", String.valueOf(service.getSuppliers().size()), "Partenaires enregistrés"));
        metrics.add(metricCard("Commandes en attente", String.valueOf(pendingOrders), "À réceptionner"));

        JPanel tables = new JPanel(new java.awt.GridLayout(2, 1, 0, 14));
        tables.setOpaque(false);
        DefaultTableModel lowStock = tableModel("Article", "Stock utilisable", "Seuil", "Unité");
        for (SupplyItem item : service.getLowStockItems()) {
            lowStock.addRow(new Object[] { item.getName(), service.getUsableQuantity(item),
                    item.getMinimumStock(), item.getUnit() });
        }
        tables.add(tableCard("Alertes de stock bas", lowStock));

        DefaultTableModel expiring = tableModel("Article", "Quantité", "Expiration", "Fournisseur");
        for (SupplyItem item : service.getItems()) {
            for (StockLot lot : service.getExpiringLots(item, 30)) {
                expiring.addRow(new Object[] { item.getName(), lot.getQuantity(), lot.getExpirationDate(),
                        service.getSupplierName(lot.getSupplierId()) });
            }
        }
        tables.add(tableCard("Lots expirant dans les 30 jours", expiring));
        panel.add(metrics, BorderLayout.NORTH);
        panel.add(tables, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInventoryPage() {
        JPanel panel = pageContainer();
        JPanel actions = actionBar("Articles et niveaux de stock");
        actions.add(actionButton("Ajouter un article", this::addItem));
        actions.add(actionButton("Réceptionner du stock", this::receiveStock));
        actions.add(actionButton("Sortir du stock", this::issueStock));

        DefaultTableModel model = tableModel("ID", "Article", "Catégorie", "Stock total",
                "Stock utilisable", "Seuil", "Unité");
        for (SupplyItem item : service.getItems()) {
            model.addRow(new Object[] { item.getId(), item.getName(), item.getCategory(),
                    service.getQuantity(item), service.getUsableQuantity(item),
                    item.getMinimumStock(), item.getUnit() });
        }
        panel.add(actions, BorderLayout.NORTH);
        panel.add(tableCard("Inventaire", model), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSuppliersPage() {
        JPanel panel = pageContainer();
        JPanel actions = actionBar("Partenaires d'approvisionnement");
        actions.add(actionButton("Ajouter un fournisseur", this::addSupplier));
        DefaultTableModel model = tableModel("ID", "Fournisseur", "Contact", "E-mail");
        for (Supplier supplier : service.getSuppliers()) {
            model.addRow(new Object[] { supplier.getId(), supplier.getName(),
                    supplier.getContact(), supplier.getEmail() });
        }
        panel.add(actions, BorderLayout.NORTH);
        panel.add(tableCard("Fournisseurs", model), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createOrdersPage() {
        JPanel panel = pageContainer();
        JPanel actions = actionBar("Suivi des commandes fournisseur");
        actions.add(actionButton("Créer une commande", this::createOrder));
        actions.add(actionButton("Réceptionner une commande", this::receiveOrder));
        DefaultTableModel model = tableModel("ID", "Date", "Fournisseur", "Articles", "Statut");
        for (PurchaseOrder order : service.getOrders()) {
            StringBuilder lines = new StringBuilder();
            for (PurchaseOrder.OrderLine line : order.getLines()) {
                if (lines.length() > 0) {
                    lines.append(", ");
                }
                lines.append(service.getItemName(line.getItemId())).append(" (")
                        .append(line.getQuantity()).append(')');
            }
            model.addRow(new Object[] { order.getId(), order.getOrderDate(),
                    service.getSupplierName(order.getSupplierId()), lines.toString(), order.getStatus() });
        }
        panel.add(actions, BorderLayout.NORTH);
        panel.add(tableCard("Commandes", model), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMovementsPage() {
        JPanel panel = pageContainer();
        JPanel actions = actionBar("Traçabilité des sorties");
        actions.add(new JLabel("Les sorties sont enregistrées avec leur destination et appliquent FEFO."));
        DefaultTableModel model = tableModel("Date et heure", "Article", "Quantité", "Destination");
        for (StockMovement movement : service.getMovements()) {
            model.addRow(new Object[] { movement.getDate(), service.getItemName(movement.getItemId()),
                    movement.getQuantity(), movement.getDestination() });
        }
        panel.add(actions, BorderLayout.NORTH);
        panel.add(tableCard("Historique des mouvements", model), BorderLayout.CENTER);
        return panel;
    }

    private JPanel pageContainer() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        return panel;
    }

    private JPanel actionBar(String title) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(TEXT);
        bar.add(label);
        return bar;
    }

    private JPanel metricCard(String title, String value, String hint) {
        JPanel card = new SurfacePanel(16);
        card.setBorder(BorderFactory.createEmptyBorder(17, 19, 17, 18));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setForeground(MUTED);
        heading.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel number = new JLabel(value);
        number.setForeground(TEXT);
        number.setFont(new Font("SansSerif", Font.BOLD, 32));
        JLabel description = new JLabel(hint);
        description.setForeground(MUTED);
        description.setFont(new Font("SansSerif", Font.PLAIN, 11));
        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(number);
        card.add(Box.createVerticalStrut(2));
        card.add(description);
        return card;
    }

    private JPanel tableCard(String title, DefaultTableModel model) {
        JPanel card = new SurfacePanel(15);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(15, 17, 15, 17));
        JLabel heading = new JLabel(title);
        heading.setForeground(TEXT);
        heading.setFont(new Font("SansSerif", Font.BOLD, 14));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setBackground(FOREST_PALE);
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(FOREST_PALE);
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focused, int row, int column) {
                Component cell = super.getTableCellRendererComponent(
                        source, value, selected, focused, row, column);
                if (!selected) {
                    cell.setBackground(WHITE);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 8));
                return cell;
            }
        };
        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);

        JPanel tableContent;
        if (model.getRowCount() == 0) {
            tableContent = new JPanel(new BorderLayout());
            tableContent.setBackground(Color.WHITE);
            JLabel empty = new JLabel("Aucune donnée à afficher pour le moment.", SwingConstants.CENTER);
            empty.setForeground(MUTED);
            empty.setFont(new Font("SansSerif", Font.PLAIN, 12));
            empty.setBorder(BorderFactory.createEmptyBorder(35, 12, 35, 12));
            tableContent.add(empty, BorderLayout.CENTER);
        } else {
            tableContent = new JPanel(new BorderLayout());
            tableContent.setBackground(Color.WHITE);
            tableContent.add(scrollPane, BorderLayout.CENTER);
        }
        card.add(heading, BorderLayout.NORTH);
        card.add(tableContent, BorderLayout.CENTER);
        return card;
    }

    private DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JButton actionButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.setBackground(FOREST_PALE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FOREST_GREEN, 2),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void addItem() {
        JTextField name = new JTextField();
        JTextField category = new JTextField();
        JTextField unit = new JTextField();
        JSpinner threshold = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        JPanel form = formPanel(
                "Nom de l'article", name,
                "Catégorie", category,
                "Unité", unit,
                "Seuil minimum", threshold);
        if (confirmForm("Ajouter un article", form)) {
            execute(() -> {
                SupplyItem item = service.addItem(name.getText(), category.getText(),
                        unit.getText(), (Integer) threshold.getValue());
                info("Article ajouté. Identifiant : " + item.getId());
            });
        }
    }

    private void addSupplier() {
        JTextField name = new JTextField();
        JTextField contact = new JTextField();
        JTextField email = new JTextField();
        JPanel form = formPanel("Nom du fournisseur", name, "Contact", contact, "E-mail", email);
        if (confirmForm("Ajouter un fournisseur", form)) {
            execute(() -> {
                Supplier supplier = service.addSupplier(name.getText(), contact.getText(), email.getText());
                info("Fournisseur ajouté. Identifiant : " + supplier.getId());
            });
        }
    }

    private void receiveStock() {
        JComboBox<SupplyItem> item = itemSelector();
        JComboBox<Supplier> supplier = supplierSelector();
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
        JTextField expiration = new JTextField(LocalDate.now().plusMonths(12).toString());
        JPanel form = formPanel("Article", item, "Fournisseur", supplier,
                "Quantité reçue", quantity, "Expiration (AAAA-MM-JJ)", expiration);
        if (confirmForm("Réceptionner du stock", form)) {
            execute(() -> service.receiveStock(selectedItem(item).getId(), (Integer) quantity.getValue(),
                    parseDate(expiration.getText()), selectedSupplier(supplier).getId()));
        }
    }

    private void issueStock() {
        JComboBox<SupplyItem> item = itemSelector();
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
        JTextField destination = new JTextField();
        JPanel form = formPanel("Article", item, "Quantité à sortir", quantity,
                "Service / destination", destination);
        if (confirmForm("Sortir du stock (FEFO)", form)) {
            execute(() -> service.issueStock(selectedItem(item).getId(),
                    (Integer) quantity.getValue(), destination.getText()));
        }
    }

    private void createOrder() {
        JComboBox<Supplier> supplier = supplierSelector();
        JComboBox<SupplyItem> item = itemSelector();
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));
        JPanel form = formPanel("Fournisseur", supplier, "Article", item, "Quantité commandée", quantity);
        if (confirmForm("Créer une commande", form)) {
            execute(() -> {
                PurchaseOrder order = service.createOrder(selectedSupplier(supplier).getId(),
                        selectedItem(item).getId(), (Integer) quantity.getValue());
                info("Commande créée. Identifiant : " + order.getId());
            });
        }
    }

    private void receiveOrder() {
        JComboBox<PurchaseOrder> order = new JComboBox<>();
        for (PurchaseOrder candidate : service.getOrders()) {
            if (candidate.getStatus() == PurchaseOrder.Status.ORDERED) {
                order.addItem(candidate);
            }
        }
        order.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getId() + " - "
                    + service.getSupplierName(value.getSupplierId()) + " - " + value.getOrderDate());
            label.setOpaque(selected);
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        addFormRow(form, constraints, 0, "Commande", order);
        Map<String, JTextField> expirationFields = new HashMap<>();
        if (order.getItemCount() > 0) {
            addExpirationFields(form, constraints, order, expirationFields);
        } else {
            JLabel none = new JLabel("Aucune commande en attente.");
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 2;
            form.add(none, constraints);
        }
        order.addActionListener(event -> {
            PurchaseOrder selected = (PurchaseOrder) order.getSelectedItem();
            expirationFields.clear();
            while (form.getComponentCount() > 2) {
                form.remove(form.getComponentCount() - 1);
            }
            if (selected != null) {
                addExpirationFields(form, constraints, order, expirationFields);
            }
            form.revalidate();
            form.repaint();
        });
        if (order.getItemCount() == 0) {
            info("Aucune commande en attente de réception.");
            return;
        }
        if (confirmForm("Réceptionner une commande", form)) {
            execute(() -> {
                PurchaseOrder selected = (PurchaseOrder) order.getSelectedItem();
                if (selected == null) {
                    throw new IllegalArgumentException("Sélectionnez une commande.");
                }
                Map<String, LocalDate> dates = new HashMap<>();
                for (PurchaseOrder.OrderLine line : selected.getLines()) {
                    JTextField date = expirationFields.get(line.getItemId());
                    if (date == null) {
                        throw new IllegalArgumentException("Date d'expiration manquante.");
                    }
                    dates.put(line.getItemId(), parseDate(date.getText()));
                }
                service.receiveOrder(selected.getId(), dates);
            });
        }
    }

    private void addExpirationFields(JPanel form, GridBagConstraints constraints,
                                     JComboBox<PurchaseOrder> order,
                                     Map<String, JTextField> expirationFields) {
        PurchaseOrder selected = (PurchaseOrder) order.getSelectedItem();
        if (selected == null) {
            return;
        }
        int row = 1;
        for (PurchaseOrder.OrderLine line : selected.getLines()) {
            JTextField date = new JTextField(LocalDate.now().plusMonths(12).toString());
            expirationFields.put(line.getItemId(), date);
            addFormRow(form, constraints, row++, service.getItemName(line.getItemId())
                    + " (" + line.getQuantity() + ") - expiration", date);
        }
    }

    private JComboBox<SupplyItem> itemSelector() {
        JComboBox<SupplyItem> selector = new JComboBox<>();
        for (SupplyItem item : service.getItems()) {
            selector.addItem(item);
        }
        selector.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getName() + " [" + value.getId() + "]");
            label.setOpaque(selected);
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });
        return selector;
    }

    private JComboBox<Supplier> supplierSelector() {
        JComboBox<Supplier> selector = new JComboBox<>();
        for (Supplier supplier : service.getSuppliers()) {
            selector.addItem(supplier);
        }
        selector.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getName() + " [" + value.getId() + "]");
            label.setOpaque(selected);
            if (selected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            return label;
        });
        return selector;
    }

    private static SupplyItem selectedItem(JComboBox<SupplyItem> selector) {
        SupplyItem item = (SupplyItem) selector.getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Ajoutez d'abord un article à l'inventaire.");
        }
        return item;
    }

    private static Supplier selectedSupplier(JComboBox<Supplier> selector) {
        Supplier supplier = (Supplier) selector.getSelectedItem();
        if (supplier == null) {
            throw new IllegalArgumentException("Ajoutez d'abord un fournisseur.");
        }
        return supplier;
    }

    private JPanel formPanel(Object... fields) {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        for (int i = 0; i < fields.length; i += 2) {
            Component input = (Component) fields[i + 1];
            styleFormInput(input);
            addFormRow(form, constraints, i / 2, (String) fields[i], input);
        }
        return form;
    }

    private void styleFormInput(Component input) {
        input.setFont(new Font("SansSerif", Font.PLAIN, 13));
        if (input instanceof JTextField textField) {
            textField.setPreferredSize(new Dimension(290, 36));
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)));
        } else if (input instanceof JComboBox<?>) {
            input.setPreferredSize(new Dimension(290, 36));
        } else if (input instanceof JSpinner spinner) {
            spinner.setPreferredSize(new Dimension(130, 36));
        }
    }

    private void addFormRow(JPanel form, GridBagConstraints constraints, int row,
                            String label, Component input) {
        constraints.gridy = row;
        constraints.gridx = 0;
        constraints.weightx = 0;
        form.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        form.add(input, constraints);
    }

    private boolean confirmForm(String title, JPanel form) {
        int result = JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("La date doit respecter le format AAAA-MM-JJ.");
        }
    }

    private void execute(Runnable operation) {
        try {
            operation.run();
            store.save(service.getData());
            String visiblePage = currentPage();
            refreshPages();
            pageLayout.show(pages, visiblePage);
        } catch (IllegalArgumentException exception) {
            error(exception.getMessage());
        } catch (IOException exception) {
            error("Impossible d'enregistrer les données : " + exception.getMessage());
        }
    }

    private String currentPage() {
        for (Map.Entry<String, JPanel> entry : pagePanels.entrySet()) {
            if (entry.getValue().isVisible()) {
                return entry.getKey();
            }
        }
        return "dashboard";
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "MediSupply", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 13));
            UIManager.put("OptionPane.buttonFont", new Font("SansSerif", Font.BOLD, 12));
            UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 12));
            UIManager.put("List.selectionForeground", Color.BLACK);
            UIManager.put("Table.selectionForeground", Color.BLACK);
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException exception) {
            System.err.println("Thème système indisponible, le thème Java par défaut sera utilisé : "
                    + exception.getMessage());
        }
    }

    private static final class SurfacePanel extends JPanel {
        private final int radius;

        private SurfacePanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.setColor(BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
