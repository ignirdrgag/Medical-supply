package com.medical.supplychain.web;

import com.medical.supplychain.model.Role;
import com.medical.supplychain.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/staff/*", "/manager/*", "/doctor/*", "/supplier/*"})
public final class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        User user = WebSupport.currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        Role required = requiredRole(path);
        if (required != null && user.getRole() != required) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private Role requiredRole(String path) {
        if (path == null) {
            return null;
        }
        if (path.startsWith("/admin/")) {
            return Role.ADMIN;
        }
        if (path.startsWith("/staff/")) {
            return Role.STAFF;
        }
        if (path.startsWith("/manager/")) {
            return Role.MANAGER;
        }
        if (path.startsWith("/doctor/")) {
            return Role.DOCTOR;
        }
        if (path.startsWith("/supplier/")) {
            return Role.SUPPLIER;
        }
        return null;
    }
}
