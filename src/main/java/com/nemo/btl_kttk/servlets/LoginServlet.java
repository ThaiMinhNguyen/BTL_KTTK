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
            User user = (User) session.getAttribute("user");
            redirectBasedOnRole(response, user);
        } else {
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("txtUsername");
        String password = request.getParameter("txtPassword");
        
        
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
            return;
        }
        
        User user = userDAO.checkLogin(username, password);
        
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            redirectBasedOnRole(response, user);
        } else {
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
        }
    }
    
    
    private void redirectBasedOnRole(HttpServletResponse response, User user) throws IOException {
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        
        switch (user.getRole()) {
            case "ADMIN":
                response.sendRedirect("gdChinhQL.jsp");
                break;
            case "MANAGER":
                response.sendRedirect("gdChinhQL.jsp");
                break;
            case "EMPLOYEE":
                response.sendRedirect("gdChinhNV.jsp");
                break;
            default:
                response.sendRedirect("gdChinhNV.jsp");
                break;
        }
    }
} 