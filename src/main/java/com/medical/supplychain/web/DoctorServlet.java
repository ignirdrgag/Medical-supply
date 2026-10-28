package com.medical.supplychain.web;

import com.medical.supplychain.service.InventoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/doctor/*")
public final class DoctorServlet extends HttpServlet {
    private final InventoryService service = new InventoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || "/".equals(path) || "/dashboard".equals(path)) {
            try {
                var items = service.getItems();
                request.setAttribute("pageTitle", "Espace medecin");
                request.setAttribute("items", items);
                request.setAttribute("lowStockCount", items.stream().filter(item -> item.isLowStock()).count());
                request.setAttribute("outOfStockCount", items.stream().filter(item -> item.isOutOfStock()).count());
                request.getRequestDispatcher("/WEB-INF/views/doctor/dashboard.jsp").forward(request, response);
            } catch (SQLException exception) {
                throw new ServletException("Impossible de charger le stock pour le medecin.", exception);
            }
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
