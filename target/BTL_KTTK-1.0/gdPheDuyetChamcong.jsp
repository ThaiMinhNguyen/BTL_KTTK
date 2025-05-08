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
    <title>Phê duyệt chấm công</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        
        .container {
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #000;
            background-color: #fff;
        }
        
        h3 {
            margin-top: 20px;
            margin-bottom: 10px;
        }
        
        .info-section {
            margin-bottom: 20px;
        }
        
        .info-item {
            margin-bottom: 10px;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
            margin-bottom: 20px;
        }
        
        th, td {
            border: 1px solid #000;
            padding: 8px;
            text-align: left;
        }
        
        .approval-button {
            display: block;
            width: 120px;
            margin: 20px auto;
            padding: 8px 0;
            background-color: #e6f2ff;
            border: 1px solid #000;
            text-align: center;
            cursor: pointer;
            font-size: 16px;
        }
        
        .approval-button:hover {
            background-color: #d4e9ff;
        }
        
        .success-message {
            color: green;
            text-align: center;
            font-weight: bold;
            margin: 10px 0;
        }
        
        .error-message {
            color: red;
            text-align: center;
            font-weight: bold;
            margin: 10px 0;
        }
    </style>
</head>
<body>
    <%
        // Lấy dữ liệu từ request
        String successMessage = (String) request.getAttribute("successMessage");
        String errorMessage = (String) request.getAttribute("errorMessage");
        User employee = (User) request.getAttribute("employee");
        Double totalHours = (Double) request.getAttribute("totalHours");
        Double amount = (Double) request.getAttribute("amount");
        Date weekStartDate = (Date) request.getAttribute("weekStartDate");
        List<TimeRecord> timeRecords = (List<TimeRecord>) request.getAttribute("timeRecords");
        Payment payment = (Payment) request.getAttribute("payment");
        
        // Format cho số và ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat hourFormat = new DecimalFormat("#,##0.00");
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");
    %>
    <div class="container">
        <% if(successMessage != null && !successMessage.isEmpty()) { %>
            <div class="success-message"><%= successMessage %></div>
        <% } %>
        
        <% if(errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="error-message"><%= errorMessage %></div>
        <% } %>
        
        <div class="info-section">
            <div class="info-item">
                <strong>Bảng công nhân viên:</strong> <%= employee != null ? employee.getName() + " (" + employee.getUsername() + ")" : "" %>
            </div>
            <div class="info-item">
                <strong>Total Hour:</strong> <%= totalHours != null ? hourFormat.format(totalHours) : "0.00" %> giờ
            </div>
            <div class="info-item">
                <strong>Amount:</strong> <%= amount != null ? moneyFormat.format(amount) : "0" %> VNĐ
            </div>
        </div>
        
        <h3>Lịch sử chấm công</h3>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Actual Start Time</th>
                    <th>Actual End Time</th>
                </tr>
            </thead>
            <tbody>
                <% if(timeRecords == null || timeRecords.isEmpty()) { %>
                    <tr>
                        <td colspan="3" style="text-align: center;">Không có dữ liệu chấm công nào.</td>
                    </tr>
                <% } else { %>
                    <% for(TimeRecord record : timeRecords) { %>
                        <tr>
                            <td><%= record.getId() %></td>
                            <td><%= dateTimeFormat.format(record.getActualStartTime()) %></td>
                            <td><%= dateTimeFormat.format(record.getActualEndTime()) %></td>
                        </tr>
                    <% } %>
                <% } %>
            </tbody>
        </table>
        
        <% if(payment != null && "PAID".equals(payment.getStatus())) { %>
            <div class="success-message">
                Đã phê duyệt thanh toán bởi: <%= payment.getProcessedBy().getUsername() %> vào 
                <%= dateTimeFormat.format(payment.getPaymentDate()) %>
            </div>
        <% } else { %>
            <form action="process-payment" method="POST">
                <input type="hidden" name="action" value="approve_payment">
                <input type="hidden" name="paymentId" value="<%= payment != null ? payment.getId() : "" %>">
                
                <button type="submit" class="approval-button">Phê duyệt</button>
            </form>
        <% } %>
    </div>
</body>
</html> 