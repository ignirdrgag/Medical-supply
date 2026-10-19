package com.medical.supplychain;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(urlPatterns = {"/", "/inventory"})
public final class SupplyChainWebServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SupplyChainService service;
    private DataStore store;

    @Override
    public void init() throws ServletException {
        String configuredPath = getServletContext().getInitParameter("dataFile");
        String userHome = System.getenv().getOrDefault("USERPROFILE", System.getProperty("user.home"));
        java.nio.file.Path dataPath = configuredPath == null || configuredPath.isBlank()
                ? java.nio.file.Path.of(userHome, ".medisupply", "data.ser")
                : java.nio.file.Path.of(configuredPath);
        store = new DataStore(dataPath);
        try {
            AppData data = store.exists() ? store.load() : new AppData();
            service = new SupplyChainService(data);
            if (!store.exists()) store.save(data);
        } catch (IOException | ClassNotFoundException e) {
            throw new ServletException("Impossible de charger les données d'inventaire", e);
        }
    }

    @Override
    protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        var out = resp.getWriter();
        out.println("<!doctype html><html lang='fr'><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<title>MediSupply — Inventaire</title><style>body{font:16px Arial;margin:2rem auto;max-width:1100px;padding:0 1rem;color:#183044}h1{color:#087e8b}section{background:#f3f8fa;padding:1rem;margin:1rem 0;border-radius:8px}form{display:flex;gap:.5rem;flex-wrap:wrap;margin:.7rem 0}input,select,button{padding:.55rem;border:1px solid #aac0ca;border-radius:4px}button{background:#087e8b;color:white;cursor:pointer}table{width:100%;border-collapse:collapse}th,td{text-align:left;padding:.55rem;border-bottom:1px solid #ccd9df}.msg{padding:.8rem;background:#e8f5e9}</style></head><body>");
        out.println("<h1>MediSupply — Gestion des stocks</h1><p>Application JEE servie par un servlet Jakarta.</p>");
        String message = req.getParameter("message");
        if (message != null) out.println("<p class='msg'>" + escape(message) + "</p>");
        out.println("<section><h2>Inventaire</h2><table><tr><th>Article</th><th>Catégorie</th><th>Stock total</th><th>Utilisable</th><th>Seuil</th></tr>");
        for (SupplyItem item : service.getItems()) {
            out.printf("<tr><td>%s</td><td>%s</td><td>%d %s</td><td>%d</td><td>%d</td></tr>%n",
                    escape(item.getName()), escape(item.getCategory()), service.getQuantity(item), escape(item.getUnit()),
                    service.getUsableQuantity(item), item.getMinimumStock());
        }
        out.println("</table></section><section><h2>Ajouter un article</h2><form method='post'><input type='hidden' name='action' value='item'>"
                + "<input name='name' placeholder='Nom' required><input name='category' placeholder='Catégorie' required><input name='unit' placeholder='Unité' required><input name='minimum' type='number' min='0' value='0' required><button>Ajouter</button></form></section>");
        out.println("<section><h2>Ajouter un fournisseur</h2><form method='post'><input type='hidden' name='action' value='supplier'>"
                + "<input name='name' placeholder='Nom' required><input name='contact' placeholder='Contact' required><input name='email' type='email' placeholder='E-mail' required><button>Ajouter</button></form><ul>");
        for (Supplier supplier : service.getSuppliers()) out.printf("<li>%s — %s (ID %s)</li>%n", escape(supplier.getName()), escape(supplier.getEmail()), escape(supplier.getId()));
        out.println("</ul></section><section><h2>Réceptionner un lot</h2><form method='post'><input type='hidden' name='action' value='receive'><select name='itemId' required><option value=''>Article</option>");
        for (SupplyItem item : service.getItems()) out.printf("<option value='%s'>%s</option>%n", escape(item.getId()), escape(item.getName()));
        out.println("</select><input name='quantity' type='number' min='1' placeholder='Quantité' required><input name='expiration' type='date' required><select name='supplierId' required><option value=''>Fournisseur</option>");
        for (Supplier supplier : service.getSuppliers()) out.printf("<option value='%s'>%s</option>%n", escape(supplier.getId()), escape(supplier.getName()));
        out.println("</select><button>Enregistrer la réception</button></form></section><section><h2>Sortie de stock</h2><form method='post'><input type='hidden' name='action' value='issue'><select name='itemId' required><option value=''>Article</option>");
        for (SupplyItem item : service.getItems()) out.printf("<option value='%s'>%s</option>%n", escape(item.getId()), escape(item.getName()));
        out.println("</select><input name='quantity' type='number' min='1' placeholder='Quantité' required><input name='destination' placeholder='Destination' required><button>Enregistrer la sortie</button></form></section></body></html>");
    }

    @Override
    protected synchronized void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        String message;
        try {
            switch (req.getParameter("action")) {
                case "item" -> service.addItem(req.getParameter("name"), req.getParameter("category"),
                        req.getParameter("unit"), Integer.parseInt(req.getParameter("minimum")));
                case "supplier" -> service.addSupplier(req.getParameter("name"), req.getParameter("contact"), req.getParameter("email"));
                case "receive" -> service.receiveStock(req.getParameter("itemId"), Integer.parseInt(req.getParameter("quantity")),
                        LocalDate.parse(req.getParameter("expiration")), req.getParameter("supplierId"));
                case "issue" -> service.issueStock(req.getParameter("itemId"), Integer.parseInt(req.getParameter("quantity")), req.getParameter("destination"));
                default -> throw new IllegalArgumentException("Action non reconnue.");
            }
            store.save(service.getData());
            message = "Opération enregistrée.";
        } catch (RuntimeException e) {
            message = e.getMessage();
        } catch (IOException e) {
            message = "Erreur de sauvegarde : " + e.getMessage();
        }
        resp.sendRedirect(req.getContextPath() + "/inventory?message=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8));
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
