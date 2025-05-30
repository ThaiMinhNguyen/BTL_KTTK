<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý chấm công</title>
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
            text-decoration: none;
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
        
        .form-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        input[type="date"], select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            margin-bottom: 10px;
        }
        
        .button {
            background-color: #5b87c7;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            display: block;
            margin: 20px auto;
            min-width: 150px;
        }
        
        .button:hover {
            background-color: #4c6ca0;
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
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        
        table th, table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        
        table th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        
        .choose-link {
            color: #5b87c7;
            text-decoration: underline;
            cursor: pointer;
        }
        
        .choose-link:hover {
            color: #4c6ca0;
        }
        
        .week-selector {
            background-color: #f9f9f9;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <%
        // Lấy dữ liệu từ request
        User user = (User) session.getAttribute("user");
        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");
        Date weekStartDate = (Date) request.getAttribute("weekStartDate");
        List<Payment> payments = (List<Payment>) request.getAttribute("payments");
        
        // Format cho số và ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat hourFormat = new DecimalFormat("#,##0.00");
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    %>
    <div class="header">
        <h1>Hệ thống quản lý ca làm việc</h1>
        <div class="user-info">
            <span>Xin chào, <%= user != null ? (user.getName() != null ? user.getName() : user.getUsername()) : "" %></span>
            <a href="logout" class="logout-btn">Đăng xuất</a>
        </div>
    </div>
    
    <div class="container">
        <% if(successMessage != null && !successMessage.isEmpty()) { %>
            <div class="message success">
                <%= successMessage %>
            </div>
        <% } %>
        
        <% if(errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="message error">
                <%= errorMessage %>
            </div>
        <% } %>
        
        <div class="content">
            <div class="title">Quản lý chấm công</div>
            
            <div class="week-selector">
                <form action="payment-management" method="GET">
                    <div class="form-group">
                        <label for="weekStartDate">Chọn ngày bắt đầu tuần:</label>
                        <input type="date" id="weekStartDate" name="weekStartDate" 
                               value="<%= weekStartDate != null ? dateFormat.format(weekStartDate) : "" %>" required>
                    </div>
                    <button type="submit" class="button">Xem chấm công</button>
                </form>
            </div>
            
            <table>
                <thead>
                    <tr>
                        <th>Id</th>
                        <th>Employee</th>
                        <th>TotalHour</th>
                        <th>Amount</th>
                        <th>Bonus</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if(payments == null || payments.isEmpty()) { %>
                        <tr>
                            <td colspan="7" style="text-align: center;">Không có dữ liệu chấm công nào cho tuần này.</td>
                        </tr>
                    <% } else { %>
                        <% for(Payment payment : payments) { %>
                            <tr>
                                <td><%= payment.getId() %></td>
                                <td><%= payment.getEmployee().getUsername() %></td>
                                <td><%= hourFormat.format(payment.getTotalHour()) %> giờ</td>
                                <td><%= moneyFormat.format(payment.getAmount()) %> VNĐ</td>
                                <td><%= payment.getBonus() > 0 ? moneyFormat.format(payment.getBonus()) + " VNĐ" : "-" %></td>
                                <td>
                                    <% if("PAID".equals(payment.getStatus())) { %>
                                        <span style="color: green;">Đã thanh toán</span>
                                    <% } else if("PENDING".equals(payment.getStatus())) { %>
                                        <span style="color: orange;">Chờ duyệt</span>
                                    <% } else { %>
                                        <%= payment.getStatus() %>
                                    <% } %>
                                </td>
                                <td>
                                    <a href="process-payment?paymentId=<%= payment.getId() %>" 
                                       class="choose-link">Choose</a>
                                </td>
                            </tr>
                        <% } %>
                    <% } %>
                </tbody>
            </table>
            
            <div style="text-align: center; margin-top: 20px;">
                <a href="gdChinhQL.jsp" class="back-button">Quay lại</a>
            </div>
        </div>
    </div>
</body>
</html> 