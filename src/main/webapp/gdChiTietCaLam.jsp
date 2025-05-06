<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết ca làm</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        
        .container {
            max-width: 800px;
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
        
        .content {
            background-color: white;
            padding: 20px;
            margin-top: 20px;
            border-radius: 4px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        
        .detail-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        
        .detail-table th, .detail-table td {
            border: 1px solid #ddd;
            padding: 10px;
        }
        
        .detail-table th {
            background-color: #f2f2f2;
            font-weight: bold;
            text-align: left;
            width: 30%;
        }
        
        .button {
            background-color: #5b87c7;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
        }
        
        .button:hover {
            background-color: #4c6ca0;
        }
        
        .register-button {
            background-color: #28a745;
            margin-top: 15px;
            display: block;
            width: 200px;
            text-align: center;
        }
        
        .register-button:hover {
            background-color: #218838;
        }
        
        .back-button {
            background-color: #757575;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin-top: 20px;
        }
        
        .back-button:hover {
            background-color: #616161;
        }
        
        .message {
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .info-box {
            background-color: #e2f0fb;
            border: 1px solid #b6dbf5;
            color: #0c5584;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        
        .warning-box {
            background-color: #fff3cd;
            border: 1px solid #ffeeba;
            color: #856404;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        
        .action-buttons {
            margin-top: 20px;
            display: flex;
            gap: 10px;
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
        
        // Format cho ngày tháng
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        // Lấy thông tin ca làm việc
        ShiftSlot shiftSlot = (ShiftSlot) request.getAttribute("shiftSlot");
        Boolean hasRegistered = (Boolean) request.getAttribute("hasRegistered");
        EmployeeShift userShift = (EmployeeShift) request.getAttribute("userShift");
    %>
    <div class="header">
        <h1>Chi tiết ca làm việc</h1>
        <div class="user-info">
            <span>Xin chào, <%= user.getName() != null ? user.getName() : user.getUsername() %></span>
            <a href="logout" class="back-button">Đăng xuất</a>
        </div>
    </div>
    
    <div class="container">
        <% if(request.getAttribute("successMessage") != null) { %>
            <div class="message success">
                <%= request.getAttribute("successMessage") %>
            </div>
        <% } %>
        
        <% if(request.getAttribute("errorMessage") != null) { %>
            <div class="message error">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        
        <div class="content">
            <% if(shiftSlot != null) { %>
                <h2>Thông tin ca làm việc</h2>
                
                <table class="detail-table">
                    <tr>
                        <th>ID ca làm</th>
                        <td><%= shiftSlot.getId() %></td>
                    </tr>
                    <tr>
                        <th>Ngày trong tuần</th>
                        <td><%= shiftSlot.getDayOfWeek() %></td>
                    </tr>
                    <tr>
                        <th>Thời gian bắt đầu</th>
                        <td><%= dateTimeFormat.format(shiftSlot.getStartTime()) %></td>
                    </tr>
                    <tr>
                        <th>Thời gian kết thúc</th>
                        <td><%= dateTimeFormat.format(shiftSlot.getEndTime()) %></td>
                    </tr>
                    <tr>
                        <th>Số nhân viên tối đa</th>
                        <td><%= shiftSlot.getMaxEmployee() %></td>
                    </tr>
                    <tr>
                        <th>Ngày bắt đầu tuần</th>
                        <td><%= dateFormat.format(shiftSlot.getWeekStartDate()) %></td>
                    </tr>
                    <tr>
                        <th>Trạng thái</th>
                        <td><%= shiftSlot.getStatus() %></td>
                    </tr>
                </table>
                
                <div class="action-buttons">
                    <% if(hasRegistered != null && hasRegistered) { %>
                        <div class="info-box">
                            Bạn đã đăng ký ca làm việc này. Thời gian đăng ký: <%= userShift.getRegistrationDate() %>
                        </div>
                    <% } else { %>
                        <form action="shift-register" method="post">
                            <input type="hidden" name="action" value="register">
                            <input type="hidden" name="shiftId" value="<%= shiftSlot.getId() %>">
                            <button type="submit" class="button register-button" id="btnDangky">Đăng ký ca làm việc</button>
                        </form>
                    <% } %>
                </div>
            <% } else { %>
                <div class="error">Không tìm thấy thông tin ca làm việc</div>
            <% } %>
        </div>
        
        <a href="shift-register" class="back-button">Quay lại</a>
    </div>
</body>
</html> 