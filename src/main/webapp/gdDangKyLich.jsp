<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký lịch làm việc</title>
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
        
        .content {
            background-color: white;
            padding: 20px;
            margin-top: 20px;
            border-radius: 4px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
        
        .title {
            font-size: 20px;
            margin-bottom: 15px;
            text-align: center;
            font-weight: bold;
        }
        
        .section {
            margin-bottom: 30px;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        
        table th, table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }
        
        table th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        
        .button {
            background-color: #5b87c7;
            color: white;
            border: none;
            padding: 8px 12px;
            margin: 5px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-block;
        }
        
        .button:hover {
            background-color: #4c6ca0;
        }
        
        .cancel-button {
            background-color: #f44336;
        }
        
        .cancel-button:hover {
            background-color: #d32f2f;
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
        
        .week-selector {
            margin-bottom: 15px;
            text-align: center;
        }
        
        .week-selector input {
            padding: 5px;
            margin-right: 10px;
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
    %>
    <div class="header">
        <h1>Hệ thống quản lý ca làm việc</h1>
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
            <div class="section">
                <div class="title">Danh sách ca làm</div>
                
                <div class="week-selector">
                    <form action="shift-register" method="get">
                        <label for="weekStartDate">Chọn tuần:</label>
                        <input type="date" id="weekStartDate" name="weekStartDate" value="<%= request.getParameter("weekStartDate") != null ? request.getParameter("weekStartDate") : dateFormat.format(new Date()) %>">
                        <button type="submit" class="button">Xem lịch</button>
                    </form>
                </div>
                
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>StartTime</th>
                            <th>EndTime</th>
                            <th>MaxEmployee</th>
                            <th>WeekStartDate</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        List<ShiftSlot> shiftSlots = (List<ShiftSlot>) request.getAttribute("shiftSlots");
                        if(shiftSlots != null && !shiftSlots.isEmpty()) {
                            for(ShiftSlot shiftSlot : shiftSlots) {
                                String registrationLink = "shift-register?action=register&shiftId=" + shiftSlot.getId();
                                String detailLink = "shiftSlotDetail?id=" + shiftSlot.getId();
                        %>
                        <tr>
                            <td><%= shiftSlot.getId() %></td>
                            <td><%= dateTimeFormat.format(shiftSlot.getStartTime()) %></td>
                            <td><%= dateTimeFormat.format(shiftSlot.getEndTime()) %></td>
                            <td><%= shiftSlot.getMaxEmployee() %></td>
                            <td><%= dateFormat.format(shiftSlot.getWeekStartDate()) %></td>
                            <td>
                                <form action="shift-register" method="post">
                                    <input type="hidden" name="action" value="register">
                                    <input type="hidden" name="shiftId" value="<%= shiftSlot.getId() %>">
                                    <button type="submit" class="button" id="btnDangky">Đăng ký</button>
                                </form>
                                <a href="<%= detailLink %>" class="button">Chi tiết</a>
                            </td>
                        </tr>
                        <% 
                            }
                        } else {
                        %>
                        <tr>
                            <td colspan="6">Không có ca làm việc nào trong tuần này</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            
            <div class="section">
                <div class="title">Danh sách ca làm đã đăng ký</div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>ShiftSlotID</th>
                            <th>RegistrationTime</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        List<EmployeeShift> employeeShifts = (List<EmployeeShift>) request.getAttribute("employeeShifts");
                        if(employeeShifts != null && !employeeShifts.isEmpty()) {
                            for(EmployeeShift employeeShift : employeeShifts) {
                        %>
                        <tr>
                            <td><%= employeeShift.getId() %></td>
                            <td><%= employeeShift.getShiftSlot().getId() %></td>
                            <td><%= employeeShift.getRegistrationDate() %></td>
                            <td>
                                <form action="shift-register" method="post">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="employeeShiftId" value="<%= employeeShift.getId() %>">
                                    <button type="submit" class="button cancel-button" id="btnHuyDangky">Hủy đăng ký</button>
                                </form>
                            </td>
                        </tr>
                        <% 
                            }
                        } else {
                        %>
                        <tr>
                            <td colspan="4">Bạn chưa đăng ký ca làm việc nào</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        
        <a href="gdChinhNV.jsp" class="back-button">Quay lại</a>
    </div>
</body>
</html> 