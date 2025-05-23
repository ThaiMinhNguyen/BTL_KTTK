<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.temporal.WeekFields" %>
<%@ page import="java.util.Locale" %>
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
            width: 200px;
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
        
        .warning-message {
            color: orange;
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
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DecimalFormat hourFormat = new DecimalFormat("#,##0.00");
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");

        double actualTotalPayment = 0;
        
        // Kiểm tra xem đã hết tuần chưa
        boolean isWeekEnded = false;
        
        if (weekStartDate != null) {
            // Chuyển Date thành LocalDate
            LocalDate startLocalDate;
            if (weekStartDate instanceof java.sql.Date) {
                // Chuyển đổi java.sql.Date sang LocalDate
                startLocalDate = ((java.sql.Date) weekStartDate).toLocalDate();
            } else {
                // Chuyển đổi java.util.Date sang LocalDate
                startLocalDate = weekStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            
            LocalDate today = LocalDate.now();
            
            // Tìm ngày chủ nhật cuối tuần (startDate + 6 ngày)
            LocalDate endOfWeek = startLocalDate.plusDays(6);
            
            // Nếu ngày hiện tại lớn hơn hoặc bằng ngày cuối tuần -> đã hết tuần
            if (today.isAfter(endOfWeek) || today.isEqual(endOfWeek)) {
                isWeekEnded = true;
            }
            
        }
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
                <strong>Tiền công dự tính:</strong> <%= amount != null ? moneyFormat.format(amount) : "0" %> VNĐ
            </div>
            <div class="info-item">
                <strong>Tuần bắt đầu:</strong> <%= weekStartDate != null ? dateFormat.format(weekStartDate) : "" %>
            </div>
            <div class="info-item">
                <strong>Trạng thái tuần:</strong> 
                <% if (isWeekEnded) { %>
                    <span style="color: green;">Đã kết thúc</span>
                <% } else { %>
                    <span style="color: red;">Chưa kết thúc</span>
                <% } %>
            </div>
        </div>
        
        <h3>Lịch sử chấm công</h3>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Actual Start Time</th>
                    <th>Actual End Time</th>
                    <th>Tổng giờ</th>
                    <th>Đi muộn</th>
                    <th>Về sớm</th>
                    <th>Làm thêm</th>
                    <th>Tiền công</th>
                </tr>
            </thead>
            <tbody>
                <% if(timeRecords == null || timeRecords.isEmpty()) { %>
                    <tr>
                        <td colspan="8" style="text-align: center;">Không có dữ liệu chấm công nào.</td>
                    </tr>
                <% } else { %>
                    <% for(TimeRecord record : timeRecords) { 
                        // Tính toán tiền công
                        double hourlyRate = employee.getHourlyRate();
                        double actualHours = java.time.Duration.between(
                            record.getActualStartTime(), 
                            record.getActualEndTime()
                        ).toMillis() / (1000.0 * 60 * 60);
                        double basePayment = actualHours * hourlyRate;
                        double totalPayment = basePayment - record.getLateFee() - record.getEarlyFee() + record.getBonus();
                    %>
                        <tr>
                            <td><%= record.getId() %></td>
                            <td><%= record.getActualStartTime() != null ? record.getActualStartTime().format(localDateTimeFormatter) : "" %></td>
                            <td><%= record.getActualEndTime() != null ? record.getActualEndTime().format(localDateTimeFormatter) : "" %></td>
                            <td><%= hourFormat.format(actualHours) %> giờ (<%= moneyFormat.format(basePayment) %> VNĐ)</td>
                            <td><%= record.getLateFee() > 0 ? "(-" + moneyFormat.format(record.getLateFee()) + " VNĐ)" : "-" %></td>
                            <td><%= record.getEarlyFee() > 0 ? "(-" + moneyFormat.format(record.getEarlyFee()) + " VNĐ)" : "-" %></td>
                            <td><%= record.getBonus() > 0 ? "(+" + moneyFormat.format(record.getBonus()) + " VNĐ)" : "-" %></td>
                            <td><%= moneyFormat.format(totalPayment) %> VNĐ</td>
                        </tr>
                    <% 
                        actualTotalPayment += totalPayment;
                        } 
                    %>
                        <tr style="font-weight: bold; background-color: #f0f0f0;">
                            <td colspan="7" style="text-align: right;">Tổng tiền thực tế:</td>
                            <td><%= moneyFormat.format(actualTotalPayment) %> VNĐ</td>
                        </tr>
                <% } %>
            </tbody>
        </table>
        
        <% if(payment != null && "PAID".equals(payment.getStatus())) { %>
            <div class="success-message">
                Đã thanh toán cho nhân viên bởi: <%= payment.getProcessedBy().getUsername() %> vào 
                <%= dateTimeFormat.format(payment.getPaymentDate()) %>
            </div>
        <% } else if(payment != null && "APPROVED".equals(payment.getStatus())) { %>
            <div class="success-message">
                Đã phê duyệt thanh toán bởi: <%= payment.getProcessedBy().getUsername() %> vào 
                <%= dateTimeFormat.format(payment.getPaymentDate()) %>
            </div>
            <form action="process-payment" method="POST">
                <input type="hidden" name="action" value="confirm_payment">
                <input type="hidden" name="paymentId" value="<%= payment.getId() %>">
                <button type="submit" class="approval-button">Xác nhận thanh toán cho nhân viên</button>
            </form>
        <% } else { %>
            <% if (isWeekEnded) { %>
                <form action="process-payment" method="POST">
                    <input type="hidden" name="action" value="approve_payment">
                    <input type="hidden" name="paymentId" value="<%= payment != null ? payment.getId() : "" %>">
                    <input type="hidden" name="actualTotalPayment" value="<%= actualTotalPayment %>">
                    
                    <button type="submit" class="approval-button">Phê duyệt</button>
                </form>
            <% } else { %>
                <div class="warning-message">
                    Không thể phê duyệt thanh toán khi tuần chấm công chưa kết thúc. Vui lòng quay lại vào cuối tuần.
                </div>
            <% } %>
        <% } %>
    </div>
</body>
</html> 