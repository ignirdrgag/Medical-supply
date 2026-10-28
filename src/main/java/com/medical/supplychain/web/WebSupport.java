package com.medical.supplychain.web;

import com.medical.supplychain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class WebSupport {
    private WebSupport() {
    }

    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("currentUser");
    }

    public static int intParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le champ " + name + " est obligatoire.");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Le champ " + name + " doit etre un nombre entier.");
        }
    }

    public static Integer optionalIntParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Le champ " + name + " doit etre un nombre entier.");
        }
    }

    public static LocalDate dateParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La date est obligatoire.");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("La date doit respecter le format AAAA-MM-JJ.");
        }
    }

    public static void success(HttpServletRequest request, String message) {
        flash(request, message, "success");
    }

    public static void error(HttpServletRequest request, String message) {
        flash(request, message, "error");
    }

    private static void flash(HttpServletRequest request, String message, String type) {
        HttpSession session = request.getSession(true);
        session.setAttribute("flash", message);
        session.setAttribute("flashType", type);
    }

    public static void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}
