package com.nemo.btl_kttk.servlets;

import com.nemo.btl_kttk.dao.UserDAO;
import com.nemo.btl_kttk.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // User already logged in, redirect to home page or dashboard
            response.sendRedirect("gdChinhNV.jsp");
        } else {
            // Forward to login page
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("txtUsername");
        String password = request.getParameter("txtPassword");
        
        // Validate input
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
            return;
        }
        
        // Check login credentials
        User user = userDAO.checkLogin(username, password);
        
        if (user != null) {
            // Login successful, create session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            // Redirect based on user role
            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect("admin/dashboard.jsp");
            } else if ("MANAGER".equals(user.getRole())) {
                response.sendRedirect("gdChinhNV.jsp");
            } else {
                response.sendRedirect("gdChinhNV.jsp");
            }
        } else {
            // Login failed
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
        }
    }
} 