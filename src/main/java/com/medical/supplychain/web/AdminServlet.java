package com.medical.supplychain.web;

import com.medical.supplychain.dao.UserDao;
import com.medical.supplychain.model.Role;
import com.medical.supplychain.model.User;
import com.medical.supplychain.service.InventoryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet("/admin/*")
public final class AdminServlet extends HttpServlet {
    private final InventoryService service = new InventoryService();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || "/".equals(path) || "/dashboard".equals(path)) {
            try {
                request.setAttribute("pageTitle", "Administration");
                request.setAttribute("users", userDao.findAll());
                request.setAttribute("suppliers", service.getSuppliers());
                request.setAttribute("items", service.getItems());
                request.setAttribute("userCount", service.countUsers());
                request.setAttribute("supplierCount", service.countSuppliers());
                request.setAttribute("itemCount", service.countItems());
                request.setAttribute("pendingOrders", service.countPendingOrders());
                request.setAttribute("movementCount", service.countMovements());
                request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
            } catch (SQLException exception) {
                throw new ServletException("Impossible de charger l'espace administration.", exception);
            }
            return;
        }
        if ("/users/edit".equals(path) || "/suppliers/edit".equals(path) || "/items/edit".equals(path)) {
            try {
                int id = WebSupport.intParam(request, "id");
                request.setAttribute("suppliers", service.getSuppliers());
                if ("/users/edit".equals(path)) {
                    request.setAttribute("editType", "user");
                    request.setAttribute("roles", java.util.List.of(Role.values()));
                    request.setAttribute("entity", userDao.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable.")));
                } else if ("/suppliers/edit".equals(path)) {
                    request.setAttribute("editType", "supplier");
                    request.setAttribute("entity", service.getSupplier(id));
                } else {
                    request.setAttribute("editType", "item");
                    request.setAttribute("entity", service.getItem(id));
                }
                request.setAttribute("pageTitle", "Modifier les donnees");
                request.getRequestDispatcher("/WEB-INF/views/admin/edit.jsp").forward(request, response);
            } catch (IllegalArgumentException exception) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (SQLException exception) {
                throw new ServletException("Impossible de charger les donnees a modifier.", exception);
            }
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        try {
            if (path == null) {
                throw new IllegalArgumentException("Action inconnue.");
            }
            switch (path) {
                case "/users/add" -> addUser(request);
                case "/users/delete" -> deleteUser(request);
                case "/users/update" -> updateUser(request);
                case "/suppliers/add" -> service.addSupplier(
                        request.getParameter("name"),
                        request.getParameter("contact"),
                        request.getParameter("email"));
                case "/suppliers/update" -> service.updateSupplier(
                        WebSupport.intParam(request, "id"), request.getParameter("name"),
                        request.getParameter("contact"), request.getParameter("email"));
                case "/suppliers/delete" -> service.deleteSupplier(WebSupport.intParam(request, "id"));
                case "/items/add" -> service.addItem(
                        request.getParameter("name"),
                        request.getParameter("category"),
                        request.getParameter("unit"),
                        WebSupport.intParam(request, "minimum"));
                case "/items/update" -> service.updateItem(
                        WebSupport.intParam(request, "id"), request.getParameter("name"),
                        request.getParameter("category"), request.getParameter("unit"),
                        WebSupport.intParam(request, "minimum"));
                case "/items/delete" -> service.deleteItem(WebSupport.intParam(request, "id"));
                default -> throw new IllegalArgumentException("Action inconnue : " + path);
            }
            WebSupport.success(request, successMessage(path));
        } catch (IllegalArgumentException exception) {
            WebSupport.error(request, exception.getMessage());
        } catch (SQLIntegrityConstraintViolationException exception) {
            if (path != null && path.endsWith("/delete")) {
                WebSupport.error(request, "Suppression impossible : cet element est utilise par des commandes, des lots ou des mouvements.");
            } else {
                WebSupport.error(request, "Cet identifiant ou cette valeur existe deja.");
            }
        } catch (SQLException exception) {
            throw new ServletException("Erreur base de donnees pendant l'operation.", exception);
        }
        WebSupport.redirect(request, response, "/admin/dashboard");
    }

    private void addUser(HttpServletRequest request) throws SQLException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        Role role = Role.from(request.getParameter("role"));
        Integer supplierId = WebSupport.optionalIntParam(request, "supplierId");

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caracteres.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Le nom complet est obligatoire.");
        }
        if (role == Role.SUPPLIER && supplierId == null) {
            throw new IllegalArgumentException("Un utilisateur fournisseur doit etre lie a un fournisseur.");
        }
        if (role != Role.SUPPLIER) {
            supplierId = null;
        }
        userDao.create(username.trim(), password, fullName.trim(), role, supplierId);
    }

    private void updateUser(HttpServletRequest request) throws SQLException {
        int id = WebSupport.intParam(request, "id");
        User existing = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        String username = request.getParameter("username");
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        Role role = Role.from(request.getParameter("role"));
        Integer supplierId = WebSupport.optionalIntParam(request, "supplierId");

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Le nom complet est obligatoire.");
        }
        if (password != null && !password.isBlank() && password.length() < 4) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caracteres.");
        }
        if (role == Role.SUPPLIER && supplierId == null) {
            throw new IllegalArgumentException("Un utilisateur fournisseur doit etre lie a un fournisseur.");
        }
        if (role != Role.SUPPLIER) {
            supplierId = null;
        }
        User current = WebSupport.currentUser(request);
        if (current != null && current.getId() == id && role != existing.getRole()) {
            throw new IllegalArgumentException("Vous ne pouvez pas modifier le role de votre propre compte.");
        }
        if (!userDao.update(id, username.trim(), password == null || password.isBlank() ? null : password,
                fullName.trim(), role, supplierId)) {
            throw new IllegalArgumentException("Utilisateur introuvable.");
        }
        if (current != null && current.getId() == id) {
            User updated = userDao.findById(id).orElseThrow();
            request.getSession(false).setAttribute("currentUser", updated);
        }
    }

    private void deleteUser(HttpServletRequest request) throws SQLException {
        int id = WebSupport.intParam(request, "id");
        User current = WebSupport.currentUser(request);
        if (current != null && current.getId() == id) {
            throw new IllegalArgumentException("Vous ne pouvez pas supprimer votre propre compte.");
        }
        if (!userDao.delete(id)) {
            throw new IllegalArgumentException("Utilisateur introuvable.");
        }
    }

    private String successMessage(String path) {
        return switch (path) {
            case "/users/add" -> "Utilisateur cree.";
            case "/users/update" -> "Utilisateur modifie.";
            case "/users/delete" -> "Utilisateur supprime.";
            case "/items/add" -> "Article ajoute.";
            case "/items/update" -> "Article modifie.";
            case "/items/delete" -> "Article supprime.";
            case "/suppliers/add" -> "Fournisseur ajoute.";
            case "/suppliers/update" -> "Fournisseur modifie.";
            case "/suppliers/delete" -> "Fournisseur supprime.";
            default -> "Operation enregistree.";
        };
    }
}
