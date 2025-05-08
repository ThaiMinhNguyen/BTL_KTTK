<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo và Công bố lịch làm việc</title>
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
        
        .create-button {
            background-color: #28a745;
        }
        
        .create-button:hover {
            background-color: #218838;
        }
        
        .publish-button {
            background-color: #28a745;
        }
        
        .publish-button:hover {
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
        
        .schedule-preview {
            background-color: #f9f9f9;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .schedule-preview h3 {
            margin-top: 0;
            color: #333;
        }
        
        .template-info {
            background-color: #f9f9f9;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .template-list {
            margin-top: 20px;
        }
        
        .submit-row {
            display: flex;
            justify-content: center;
            margin-top: 20px;
        }
    </style>
    <script>
        //Tự động submit form khi chọn schedule để hiện thông tin 
        function loadWorkScheduleDetails() {
            document.getElementById("loadScheduleForm").submit();
        }
    </script>
</head>
<body>
    <%
        User user = (User) session.getAttribute("user");
        if (user == null || "EMPLOYEE".equals(user.getRole())) {
            response.sendRedirect("login");
            return;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        // Lấy dữ liệu từ request
        List<WorkSchedule> schedules = (List<WorkSchedule>) request.getAttribute("schedules");
        List<SlotTemplate> templates = (List<SlotTemplate>) request.getAttribute("templates");
        
        WorkSchedule selectedSchedule = (WorkSchedule) request.getAttribute("selectedSchedule");
        List<SlotTemplate> scheduleTemplates = (List<SlotTemplate>) request.getAttribute("scheduleTemplates");
    %>
    <div class="header">
        <h1>Hệ thống quản lý ca làm việc</h1>
        <div class="user-info">
            <span>Xin chào, <%= user.getName() != null ? user.getName() : user.getUsername() %></span>
            <a href="logout" class="logout-btn">Đăng xuất</a>
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
            <div class="title">Công bố lịch làm việc</div>
            
            <form id="loadScheduleForm" action="publish-schedule" method="get">
                <div class="form-group">
                    <label for="scheduleId">Chọn lịch làm việc:</label>
                    <select id="scheduleId" name="scheduleId" onchange="loadWorkScheduleDetails()" required>
                        <option value="">-- Chọn lịch làm việc --</option>
                        <% if(schedules != null) { 
                            for(WorkSchedule schedule : schedules) { %>
                                <option value="<%= schedule.getId() %>" <%= (selectedSchedule != null && selectedSchedule.getId() == schedule.getId()) ? "selected" : "" %>>
                                    <%= schedule.getName() %>
                                </option>
                            <% } 
                        } %>
                    </select>
                </div>
            </form>
            
            <% if(selectedSchedule != null) { %>
                <div class="schedule-preview">
                    <h3>Thông tin lịch làm việc</h3>
                    <table>
                        <tr>
                            <th>ID</th>
                            <td><%= selectedSchedule.getId() %></td>
                        </tr>
                        <tr>
                            <th>Tên lịch</th>
                            <td><%= selectedSchedule.getName() %></td>
                        </tr>
                        <tr>
                            <th>Ngày tạo</th>
                            <td><%= dateFormat.format(selectedSchedule.getCreateDate()) %></td>
                        </tr>
                        <tr>
                            <th>Người tạo</th>
                            <td><%= selectedSchedule.getCreatedBy().getName() != null ? selectedSchedule.getCreatedBy().getName() : selectedSchedule.getCreatedBy().getUsername() %></td>
                        </tr>
                    </table>
                    
                    <div class="template-list">
                        <h3>Các mẫu ca làm việc trong lịch</h3>
                        <table>
                            <thead>
                                <tr>
                                    <th>Thứ trong tuần</th>
                                    <th>Thời gian bắt đầu</th>
                                    <th>Thời gian kết thúc</th>
                                    <th>Số nhân viên tối đa</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if(scheduleTemplates != null && !scheduleTemplates.isEmpty()) { 
                                    for(SlotTemplate template : scheduleTemplates) { %>
                                        <tr>
                                            <td><%= template.getDayOfWeek() %></td>
                                            <td><%= timeFormat.format(template.getStartTime()) %></td>
                                            <td><%= timeFormat.format(template.getEndTime()) %></td>
                                            <td><%= template.getMaxEmployee() %></td>
                                        </tr>
                                    <% } 
                                } else { %>
                                    <tr>
                                        <td colspan="4">Không có mẫu ca nào</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Form công bố lịch -->
                    <form action="publish-schedule" method="post">
                        <input type="hidden" name="action" value="publish_schedule">
                        <input type="hidden" name="scheduleId" value="<%= selectedSchedule.getId() %>">
                        
                        <div class="form-group">
                            <label for="weekStartDate">Chọn ngày bắt đầu tuần:</label>
                            <input type="date" id="weekStartDate" name="weekStartDate" required>
                        </div>
                        
                        <div class="submit-row">
                            <button type="submit" class="button publish-button">Tạo lịch làm việc</button>
                        </div>
                    </form>
                </div>
            <% } %>
            
            <div style="text-align: center; margin-top: 20px;">
                <a href="gdQuanLyLich.jsp" class="back-button">Quay lại</a>
            </div>
        </div>
    </div>
</body>
</html> 