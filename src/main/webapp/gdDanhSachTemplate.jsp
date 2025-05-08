<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách Template</title>
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
        
        .button {
            background-color: #5b87c7;
            color: white;
            border: none;
            padding: 8px 12px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-block;
            margin: 2px;
        }
        
        .button:hover {
            background-color: #4c6ca0;
        }
        
        .create-button {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            text-decoration: none;
            display: block;
            margin: 20px auto;
            width: fit-content;
        }
        
        .create-button:hover {
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
        
        .selected-templates {
            background-color: #f9f9f9;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .selected-templates h3 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #333;
        }
        
        .template-item {
            background-color: #e9ecef;
            padding: 10px;
            margin-bottom: 5px;
            border-radius: 4px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        input[type="text"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <%
         User user = (User) session.getAttribute("user");
        if (user == null || "EMPLOYEE".equals(user.getRole())) {
            response.sendRedirect("login");
            return;
        }
        
        // Lấy danh sách template từ request
        List<SlotTemplate> templates = (List<SlotTemplate>) request.getAttribute("templates");
        List<SlotTemplate> selectedTemplates = (List<SlotTemplate>) request.getAttribute("selectedTemplates");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
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
            <div class="title">Danh sách Template</div>
            
            <a href="create-template" class="create-button">Tạo Template mới</a>
            
            <% if(selectedTemplates != null && !selectedTemplates.isEmpty()) { %>
                <div class="selected-templates">
                    <h3>Template đã chọn</h3>
                    <% for(SlotTemplate template : selectedTemplates) { %>
                        <div class="template-item">
                            <div>
                                <strong><%= template.getDayOfWeek() %></strong> 
                                (<%= timeFormat.format(template.getStartTime()) %> - <%= timeFormat.format(template.getEndTime()) %>)
                                - Max: <%= template.getMaxEmployee() %> nhân viên
                            </div>
                            <a href="template-list?removeTemplateId=<%= template.getId() %>" class="button" style="background-color: #dc3545;">Xóa</a>
                        </div>
                    <% } %>
                    
                    <form action="publish-schedule" method="post" style="margin-top: 20px;">
                        <input type="hidden" name="action" value="create_schedule_from_templates">
                        
                        <div class="form-group">
                            <label for="scheduleName">Tên lịch làm việc:</label>
                            <input type="text" id="scheduleName" name="scheduleName" required>
                        </div>
                        
                        <button type="submit" class="create-button">Tạo lịch làm việc</button>
                    </form>
                </div>
            <% } %>
            
            <% if(templates == null || templates.isEmpty()) { %>
                <p>Không có template nào.</p>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Thứ trong tuần</th>
                            <th>Thời gian bắt đầu</th>
                            <th>Thời gian kết thúc</th>
                            <th>Số nhân viên tối đa</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for(SlotTemplate template : templates) { %>
                            <tr>
                                <td><%= template.getDayOfWeek() %></td>
                                <td><%= timeFormat.format(template.getStartTime()) %></td>
                                <td><%= timeFormat.format(template.getEndTime()) %></td>
                                <td><%= template.getMaxEmployee() %></td>
                                <td>
                                    <a href="template-list?addTemplateId=<%= template.getId() %>" class="button">Chọn</a>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
            
            <div style="text-align: center; margin-top: 20px;">
                <a href="schedule-management" class="back-button">Quay lại</a>
            </div>
        </div>
    </div>
</body>
</html> 