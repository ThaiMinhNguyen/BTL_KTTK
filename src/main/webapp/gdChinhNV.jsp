<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nemo.btl_kttk.models.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chính nhân viên</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .header {
            background-color: #4c6ca0;
            color: white;
            padding: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .header h1 {
            margin: 0;
            font-size: 24px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
        }
        
        .user-info span {
            margin-right: 10px;
        }
        
        .logout-btn {
            background-color: #f44336;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
        }
        
        .content {
            background-color: white;
            padding: 20px;
            margin-top: 20px;
            border-radius: 4px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        
        .title {
            font-size: 24px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .functions {
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        
        .function-btn {
            background-color: #5b87c7;
            color: white;
            border: none;
            padding: 12px 20px;
            margin: 10px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            width: 200px;
            text-align: center;
            text-decoration: none;
        }
        
        .function-btn:hover {
            background-color: #4c6ca0;
        }
    </style>
</head>
<body>
    <%
        // Kiểm tra đăng nhập
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
    %>
    <div class="header">
        <h1>Hệ thống quản lý ca làm việc</h1>
        <div class="user-info">
            <span>Xin chào, <%= user.getName() != null ? user.getName() : user.getUsername() %></span>
            <a href="logout" class="logout-btn">Đăng xuất</a>
        </div>
    </div>
    
    <div class="container">
        <div class="content">
            <div class="title">Các chức năng của nhân viên</div>
            
            <div class="functions">
                <a href="shift-register" class="function-btn" id="btnDangKyLich">Đăng ký lịch</a>
                <% if ("MANAGER".equals(user.getRole()) || "ADMIN".equals(user.getRole())) { %>
                    <a href="manage-shifts" class="function-btn">Quản lý ca làm việc</a>
                    <a href="payment-management" class="function-btn">Quản lý thanh toán</a>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html> 