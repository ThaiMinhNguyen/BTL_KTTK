<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Template</title>
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
        
        input[type="text"], select, input[type="time"], input[type="number"] {
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
        
        .save-button {
            background-color: #28a745;
            padding: 12px 20px;
            font-size: 18px;
            min-width: 200px;
        }
        
        .save-button:hover {
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
        
        .template-box {
            border: 1px solid #ddd;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 4px;
            background-color: #f9f9f9;
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
            <div class="title">Tạo Template</div>
            
            <form action="create-template" method="post">
                <input type="hidden" name="action" value="create_template">
                
                <div class="template-box">
                    <h3>Thông tin ca làm việc</h3>
                    
                    <div class="form-group">
                        <label for="dayOfWeek">Ngày trong tuần:</label>
                        <select id="dayOfWeek" name="dayOfWeek" required>
                            <option value="">-- Chọn ngày --</option>
                            <option value="MONDAY">Thứ hai (Monday)</option>
                            <option value="TUESDAY">Thứ ba (Tuesday)</option>
                            <option value="WEDNESDAY">Thứ tư (Wednesday)</option>
                            <option value="THURSDAY">Thứ năm (Thursday)</option>
                            <option value="FRIDAY">Thứ sáu (Friday)</option>
                            <option value="SATURDAY">Thứ bảy (Saturday)</option>
                            <option value="SUNDAY">Chủ nhật (Sunday)</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="startTime">Giờ bắt đầu:</label>
                        <input type="time" id="startTime" name="startTime" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="endTime">Giờ kết thúc:</label>
                        <input type="time" id="endTime" name="endTime" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="maxEmployee">Số nhân viên tối đa:</label>
                        <input type="number" id="maxEmployee" name="maxEmployee" min="1" required>
                    </div>
                </div>
                
                <button type="submit" class="button save-button">Lưu Template</button>
            </form>
            
            <div style="text-align: center; margin-top: 20px;">
                <a href="template-list" class="back-button">Quay lại</a>
            </div>
        </div>
    </div>
</body>
</html> 