package com.medical.supplychain.web;

import com.medical.supplychain.model.User;
import com.medical.supplychain.service.InventoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/manager/*")
public final class ManagerServlet extends HttpServlet {
    private final InventoryService service = new InventoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || "/".equals(path) || "/dashboard".equals(path)) {
            try {
                request.setAttribute("pageTitle", "Espace responsable");
                request.setAttribute("items", service.getItems());
                request.setAttribute("suppliers", service.getSuppliers());
                request.setAttribute("orders", service.getOrders());
                request.setAttribute("lowStockItems", service.getLowStockItems());
                request.setAttribute("expiringLots", service.getExpiringLots(30));
                request.setAttribute("itemCount", service.countItems());
                request.setAttribute("supplierCount", service.countSuppliers());
                request.setAttribute("pendingOrders", service.countPendingOrders());
                request.setAttribute("movementCount", service.countMovements());
                request.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(request, response);
            } catch (SQLException exception) {
                throw new ServletException("Impossible de charger l'espace responsable.", exception);
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
            switch (path) {
                case "/orders/create" -> {
                    int orderId = service.createOrder(
                            WebSupport.intParam(request, "supplierId"),
                            WebSupport.intParam(request, "itemId"),
                            WebSupport.intParam(request, "quantity"),
                            user.getId());
                    WebSupport.success(request, "Commande n°" + orderId + " creee.");
                }
                case "/orders/receive" -> {
                    service.receiveOrder(
                            WebSupport.intParam(request, "orderId"),
                            WebSupport.dateParam(request, "expiration"),
                            user.getId());
                    WebSupport.success(request, "Commande receptionnee et stock mis a jour.");
                }
                default -> throw new IllegalArgumentException("Action inconnue : " + path);
            }
        } catch (IllegalArgumentException exception) {
            WebSupport.error(request, exception.getMessage());
        } catch (SQLException exception) {
            throw new ServletException("Erreur base de donnees pendant l'operation de commande.", exception);
        }
        WebSupport.redirect(request, response, "/manager/dashboard");
    }
}
