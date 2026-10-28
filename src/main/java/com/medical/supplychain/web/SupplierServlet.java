package com.medical.supplychain.web;

import com.medical.supplychain.model.StockLot;
import com.medical.supplychain.model.User;
import com.medical.supplychain.service.InventoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/supplier/*")
public final class SupplierServlet extends HttpServlet {
    private final InventoryService service = new InventoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || "/".equals(path) || "/dashboard".equals(path)) {
            User user = WebSupport.currentUser(request);
            if (user.getSupplierId() == null) {
                request.setAttribute("pageTitle", "Espace fournisseur");
                request.setAttribute("noSupplier", true);
                request.getRequestDispatcher("/WEB-INF/views/supplier/dashboard.jsp").forward(request, response);
                return;
            }
            try {
                List<StockLot> lots = service.getLotsForSupplier(user.getSupplierId());
                var orders = service.getOrdersForSupplier(user.getSupplierId());
                request.setAttribute("pageTitle", "Espace fournisseur");
                request.setAttribute("supplier", service.getSupplier(user.getSupplierId()));
                request.setAttribute("lots", lots);
                request.setAttribute("orders", orders);
                request.setAttribute("totalQuantity", lots.stream().mapToInt(StockLot::getQuantity).sum());
                request.setAttribute("expiringSoon",
                        lots.stream().filter(StockLot::isExpiringSoon).count());
                request.setAttribute("lotCount", lots.size());
                request.setAttribute("pendingOrders",
                        orders.stream().filter(order -> order.isPending()).count());
                request.getRequestDispatcher("/WEB-INF/views/supplier/dashboard.jsp").forward(request, response);
            } catch (SQLException exception) {
                throw new ServletException("Impossible de charger l'espace fournisseur.", exception);
            }
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        User user = WebSupport.currentUser(request);
        try {
            if (path == null) {
                throw new IllegalArgumentException("Action inconnue.");
            }
            if (!"/profile".equals(path)) {
                throw new IllegalArgumentException("Action inconnue : " + path);
            }
            if (user.getSupplierId() == null) {
                throw new IllegalArgumentException("Votre compte n'est lie a aucun fournisseur.");
            }
            service.updateSupplierContact(
                    user.getSupplierId(),
                    request.getParameter("contact"),
                    request.getParameter("email"));
            WebSupport.success(request, "Coordonnees mises a jour.");
        } catch (IllegalArgumentException exception) {
            WebSupport.error(request, exception.getMessage());
        } catch (SQLException exception) {
            throw new ServletException("Erreur base de donnees pendant la mise a jour du profil.", exception);
        }
        WebSupport.redirect(request, response, "/supplier/dashboard");
    }
}
