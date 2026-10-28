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

@WebServlet("/staff/*")
public final class StaffServlet extends HttpServlet {
    private final InventoryService service = new InventoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || "/".equals(path) || "/dashboard".equals(path)) {
            try {
                request.setAttribute("pageTitle", "Espace agent de stock");
                request.setAttribute("items", service.getItems());
                request.setAttribute("suppliers", service.getSuppliers());
                request.setAttribute("movements", service.getMovements(50));
                request.setAttribute("lowStockItems", service.getLowStockItems());
                request.setAttribute("expiringLots", service.getExpiringLots(30));
                request.setAttribute("movementCount", service.countMovements());
                request.getRequestDispatcher("/WEB-INF/views/staff/dashboard.jsp").forward(request, response);
            } catch (SQLException exception) {
                throw new ServletException("Impossible de charger l'espace agent de stock.", exception);
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
                case "/receive" -> {
                    service.receiveStock(
                            WebSupport.intParam(request, "itemId"),
                            WebSupport.intParam(request, "supplierId"),
                            WebSupport.intParam(request, "quantity"),
                            WebSupport.dateParam(request, "expiration"),
                            user.getId());
                    WebSupport.success(request, "Lot receptionne et ajoute au stock.");
                }
                case "/issue" -> {
                    service.issueStock(
                            WebSupport.intParam(request, "itemId"),
                            WebSupport.intParam(request, "quantity"),
                            request.getParameter("destination"),
                            user.getId());
                    WebSupport.success(request, "Sortie de stock enregistree (FEFO).");
                }
                default -> throw new IllegalArgumentException("Action inconnue : " + path);
            }
        } catch (IllegalArgumentException exception) {
            WebSupport.error(request, exception.getMessage());
        } catch (SQLException exception) {
            throw new ServletException("Erreur base de donnees pendant l'operation de stock.", exception);
        }
        WebSupport.redirect(request, response, "/staff/dashboard");
    }
}
