package com.medical.supplychain.web;

import com.medical.supplychain.dao.UserDao;
import com.medical.supplychain.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/login")
public final class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = WebSupport.currentUser(request);
        if (user != null) {
            WebSupport.redirect(request, response, user.getHomePath());
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Optional<User> candidate = userDao.findByUsername(username == null ? "" : username.trim());
            if (candidate.isEmpty() || !candidate.get().getPassword().equals(password)) {
                request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect.");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            User user = candidate.get();
            HttpSession session = request.getSession(true);
            request.changeSessionId();
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30 * 60);
            WebSupport.redirect(request, response, user.getHomePath());
        } catch (SQLException exception) {
            throw new ServletException("Impossible de contacter la base de donnees MySQL.", exception);
        }
    }
}
