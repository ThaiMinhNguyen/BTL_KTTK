<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.nemo.btl_kttk.models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký Lịch Cho Nhân Viên</title>
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
        
        .form-section {
            background-color: #f9f9f9;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .form-section h3 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #333;
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
        
        .search-button {
            background-color: #28a745;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 10px;
        }
        
        .search-button:hover {
            background-color: #218838;
        }
        
        .assign-button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 12px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            width: 100%;
        }
        
        .assign-button:hover {
            background-color: #0056b3;
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
        
        .shifts-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .shift-card {
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 15px;
            background-color: #f9f9f9;
        }
        
        .shift-card h4 {
            margin-top: 0;
            color: #333;
            font-size: 18px;
        }
        
        .shift-info {
            margin-bottom: 15px;
        }
        
        .shift-info p {
            margin: 5px 0;
            color: #666;
        }
        
        .time-info {
            background-color: #e9ecef;
            padding: 8px;
            border-radius: 4px;
            font-weight: bold;
            color: #495057;
            margin-bottom: 10px;
        }
        
        .max-employee {
            background-color: #cce5ff;
            padding: 5px 10px;
            border-radius: 15px;
            font-size: 12px;
            color: #0066cc;
            display: inline-block;
        }
        
        .instruction-section {
            text-align: center;
            padding: 40px 20px;
        }
        
        .instruction-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
            margin-top: 30px;
        }
        
        .instruction-item {
            padding: 20px;
            background-color: #f9f9f9;
            border-radius: 4px;
        }
        
        .instruction-item h4 {
            color: #333;
            margin-bottom: 10px;
        }
        
        .instruction-item p {
            color: #666;
            margin: 0;
        }
        
        .no-shifts {
            text-align: center;
            color: #666;
            font-style: italic;
            padding: 20px;
        }
        
        .registered-section {
            margin-top: 15px;
            padding: 10px;
            background-color: #f0f8ff;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
        }
        
        .registered-section h5 {
            margin: 0 0 10px 0;
            color: #0066cc;
            font-size: 14px;
        }
        
        .registered-employee {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 5px 0;
            border-bottom: 1px solid #e6f3ff;
        }
        
        .registered-employee:last-child {
            border-bottom: none;
        }
        
        .employee-name {
            font-size: 13px;
            color: #333;
        }
        
        .cancel-btn {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 3px 8px;
            border-radius: 3px;
            cursor: pointer;
            font-size: 11px;
            text-decoration: none;
        }
        
        .cancel-btn:hover {
            background-color: #c82333;
        }
        
        .no-registered {
            font-style: italic;
            color: #999;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <%
        // Kiểm tra đăng nhập
        User user = (User) session.getAttribute("user");
        if (user == null || !"MANAGER".equals(user.getRole())) {
            response.sendRedirect("login");
            return;
        }
        
        // Lấy dữ liệu từ request
        List<User> employees = (List<User>) request.getAttribute("employees");
        List<ShiftSlot> shiftSlots = (List<ShiftSlot>) request.getAttribute("shiftSlots");
        Map<Integer, List<User>> availableEmployeesMap = (Map<Integer, List<User>>) request.getAttribute("availableEmployeesMap");
        Map<Integer, Integer> registeredCountMap = (Map<Integer, Integer>) request.getAttribute("registeredCountMap");
        Map<Integer, List<User>> registeredEmployeesMap = (Map<Integer, List<User>>) request.getAttribute("registeredEmployeesMap");
        String selectedDate = (String) request.getAttribute("selectedDate");
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
            <div class="title">Đăng Ký Lịch Cho Nhân Viên</div>
            
            <!-- Form chọn ngày -->
            <div class="form-section">
                <h3>Chọn Ngày Làm Việc</h3>
                <form method="post" action="manager-assign-shift">
                    <input type="hidden" name="action" value="get_shifts">
                    <div class="form-group">
                        <label for="selectedDate">Ngày làm việc:</label>
                        <input type="date" id="selectedDate" name="selectedDate" 
                               value="<%= selectedDate != null ? selectedDate : "" %>" required>
                    </div>
                    <button type="submit" class="search-button">Tìm Ca Làm Việc</button>
                </form>
            </div>
            
            <!-- Danh sách ca làm việc -->
            <% if (shiftSlots != null && !shiftSlots.isEmpty()) { %>
                <div class="form-section">
                    <h3>Ca Làm Việc - 
                        <% if (selectedDate != null) { 
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = inputFormat.parse(selectedDate);
                                out.print(outputFormat.format(date));
                            } catch (Exception e) {
                                out.print(selectedDate);
                            }
                        } %>
                    </h3>
                    
                    <div class="shifts-grid">
                        <% for (ShiftSlot shift : shiftSlots) { 
                            List<User> availableEmployees = availableEmployeesMap != null ? availableEmployeesMap.get(shift.getId()) : null;
                            Integer registeredCount = registeredCountMap != null ? registeredCountMap.get(shift.getId()) : 0;
                            List<User> registeredEmployees = registeredEmployeesMap != null ? registeredEmployeesMap.get(shift.getId()) : null;
                         %>
                            <div class="shift-card">
                                <h4>Ca <%= shift.getSlotTemplate().getDayOfWeek() %></h4>
                                
                                <div class="shift-info">
                                    <div class="time-info">
                                        <% 
                                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                            String startTime = timeFormat.format(shift.getStartTime());
                                            String endTime = timeFormat.format(shift.getEndTime());
                                        %>
                                        ⏰ <%= startTime %> - <%= endTime %>
                                    </div>
                                    
                                    <p>📍 Template: <%= shift.getSlotTemplate().getDayOfWeek() %> 
                                       (<%= timeFormat.format(shift.getSlotTemplate().getStartTime()) %> - 
                                        <%= timeFormat.format(shift.getSlotTemplate().getEndTime()) %>)</p>
                                    <p><span class="max-employee">Đã đăng ký: <%= registeredCount %> / <%= shift.getMaxEmployee() %></span></p>
                                    
                                    <% if (registeredCount >= shift.getMaxEmployee()) { %>
                                        <p style="color: #dc3545; font-weight: bold;">⚠️ Ca này đã đầy</p>
                                    <% } %>
                                </div>
                                
                                <% if (availableEmployees != null && !availableEmployees.isEmpty() && registeredCount < shift.getMaxEmployee()) { %>
                                    <form method="post" action="manager-assign-shift" onsubmit="return confirmAssign(this)">
                                        <input type="hidden" name="action" value="assign_shift">
                                        <input type="hidden" name="shiftId" value="<%= shift.getId() %>">
                                        <input type="hidden" name="selectedDate" value="<%= selectedDate %>">
                                        
                                        <div class="form-group">
                                            <label>Chọn nhân viên:</label>
                                            <select name="employeeId" required>
                                                <option value="">-- Chọn nhân viên --</option>
                                                <% for (User employee : availableEmployees) { %>
                                                    <option value="<%= employee.getId() %>">
                                                        <%= employee.getName() %> (<%= employee.getUsername() %>)
                                                    </option>
                                                <% } %>
                                            </select>
                                        </div>
                                        
                                        <button type="submit" class="assign-button">Đăng Ký Ca Này</button>
                                    </form>
                                <% } else if (registeredCount >= shift.getMaxEmployee()) { %>
                                    <div style="text-align: center; padding: 15px; color: #666; font-style: italic;">
                                        Ca này đã đủ người
                                    </div>
                                <% } else { %>
                                    <div style="text-align: center; padding: 15px; color: #666; font-style: italic;">
                                        Tất cả nhân viên đã đăng ký ca này
                                    </div>
                                <% } %>
                                
                                <!-- Danh sách người đã đăng ký -->
                                <div class="registered-section">
                                    <h5>👥 Danh sách người đã đăng ký:</h5>
                                    <% if (registeredEmployees != null && !registeredEmployees.isEmpty()) { %>
                                        <% for (User employee : registeredEmployees) { %>
                                            <div class="registered-employee">
                                                <span class="employee-name">
                                                    <%= employee.getName() %> (<%= employee.getUsername() %>)
                                                </span>
                                                <form method="post" action="manager-assign-shift" style="margin: 0; display: inline;" 
                                                      onsubmit="return confirmCancel('<%= employee.getName() %>')">
                                                    <input type="hidden" name="action" value="cancel_registration">
                                                    <input type="hidden" name="employeeId" value="<%= employee.getId() %>">
                                                    <input type="hidden" name="shiftId" value="<%= shift.getId() %>">
                                                    <input type="hidden" name="selectedDate" value="<%= selectedDate %>">
                                                    <button type="submit" class="cancel-btn">Hủy</button>
                                                </form>
                                            </div>
                                        <% } %>
                                    <% } else { %>
                                        <div class="no-registered">Chưa có nhân viên nào đăng ký</div>
                                    <% } %>
                                </div>
                            </div>
                        <% } %>
                    </div>
                </div>
            <% } else if (selectedDate != null) { %>
                <div class="form-section">
                    <div class="no-shifts">
                        Không có ca làm việc nào trong ngày này
                    </div>
                </div>
            <% } else { %>
                <!-- Hướng dẫn sử dụng -->
                <div class="instruction-section">
                    <h3>Hướng Dẫn Sử Dụng</h3>
                    <div class="instruction-grid">
                        <div class="instruction-item">
                            <h4>Bước 1: Chọn Ngày</h4>
                            <p>Chọn ngày bạn muốn đăng ký ca cho nhân viên</p>
                        </div>
                        <div class="instruction-item">
                            <h4>Bước 2: Xem Ca Làm Việc</h4>
                            <p>Xem danh sách các ca làm việc có sẵn trong ngày</p>
                        </div>
                        <div class="instruction-item">
                            <h4>Bước 3: Đăng Ký</h4>
                            <p>Chọn nhân viên và đăng ký ca làm việc cho họ</p>
                        </div>
                    </div>
                </div>
            <% } %>
            
            <div style="text-align: center;">
                <a href="gdChinhQL.jsp" class="back-button">Quay lại</a>
            </div>
        </div>
    </div>

    <script>
        // Tự động submit form khi chọn ngày
        document.getElementById('selectedDate').addEventListener('change', function() {
            this.form.submit();
        });

        // Xác nhận trước khi đăng ký ca
        function confirmAssign(form) {
            const employeeSelect = form.querySelector('select[name="employeeId"]');
            const employeeName = employeeSelect.options[employeeSelect.selectedIndex].text;
            
            if (employeeSelect.value === '') {
                alert('Vui lòng chọn nhân viên!');
                return false;
            }
            
            return confirm('Bạn có chắc chắn muốn đăng ký ca này cho ' + employeeName + '?');
        }
        
        // Xác nhận trước khi hủy đăng ký
        function confirmCancel(employeeName) {
            return confirm('Bạn có chắc chắn muốn hủy đăng ký ca này cho ' + employeeName + '?');
        }
    </script>
</body>
</html> 