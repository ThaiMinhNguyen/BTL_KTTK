package com.nemo.btl_kttk.servlets;

import com.nemo.btl_kttk.dao.EmployeeShiftDAO;
import com.nemo.btl_kttk.dao.ShiftSlotDAO;
import com.nemo.btl_kttk.dao.UserDAO;
import com.nemo.btl_kttk.models.ShiftSlot;
import com.nemo.btl_kttk.models.User;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ManagerShiftAssignmentServlet", urlPatterns = {"/manager-assign-shift"})
public class ManagerShiftAssignmentServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private ShiftSlotDAO shiftSlotDAO;
    private EmployeeShiftDAO employeeShiftDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        shiftSlotDAO = new ShiftSlotDAO();
        employeeShiftDAO = new EmployeeShiftDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null || !"MANAGER".equals(currentUser.getRole())) {
            response.sendRedirect("gdDangnhap.jsp?error=access_denied");
            return;
        }
        
        showAssignShiftForm(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null || !"MANAGER".equals(currentUser.getRole())) {
            response.sendRedirect("gdDangnhap.jsp?error=access_denied");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("assign_shift".equals(action)) {
            assignShiftToEmployee(request, response);
        } else if ("get_shifts".equals(action)) {
            getShiftsByDate(request, response);
        } else {
            showAssignShiftForm(request, response);
        }
    }
    
    private void showAssignShiftForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> employees = userDAO.getAllEmployees();
        request.setAttribute("employees", employees);
        
        request.getRequestDispatcher("gdManagerAssignShift.jsp").forward(request, response);
    }
    
    private void getShiftsByDate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedDate = request.getParameter("selectedDate");
        
        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng chọn ngày");
            showAssignShiftForm(request, response);
            return;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(selectedDate);
            
            //lấy ShiftSlot theo ngày
            List<ShiftSlot> shiftSlots = shiftSlotDAO.getAvailableShifts(date);
            
            List<User> allEmployees = userDAO.getAllEmployees();
            
            //tạo Map lưu employee chưa đăng ký cho từng shift
            Map<Integer, List<User>> availableEmployeesMap = new HashMap<>();
            Map<Integer, Integer> registeredCountMap = new HashMap<>();
            
            //lấy employee chưa đăng ký
            for (ShiftSlot shift : shiftSlots) {
                //lấy employee đã đăng ký ca này
                List<User> registeredEmployees = employeeShiftDAO.getEmployeesRegisteredForShift(shift.getId());
                
                List<User> availableEmployees = new ArrayList<>();
                for (User employee : allEmployees) {
                    boolean isRegistered = false;
                    for (User registered : registeredEmployees) {
                        if (employee.getId() == registered.getId()) {
                            isRegistered = true;
                            break;
                        }
                    }
                    if (!isRegistered) {
                        availableEmployees.add(employee);
                    }
                }
                
                availableEmployeesMap.put(shift.getId(), availableEmployees);
                registeredCountMap.put(shift.getId(), registeredEmployees.size());
            }
            
            request.setAttribute("shiftSlots", shiftSlots);
            request.setAttribute("availableEmployeesMap", availableEmployeesMap);
            request.setAttribute("registeredCountMap", registeredCountMap);
            request.setAttribute("selectedDate", selectedDate);
            
        } catch (ParseException e) {
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ");
        }
        
        request.getRequestDispatcher("gdManagerAssignShift.jsp").forward(request, response);
    }
    
    private void assignShiftToEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String employeeIdStr = request.getParameter("employeeId");
        String shiftIdStr = request.getParameter("shiftId");
        String selectedDate = request.getParameter("selectedDate");
        
        if (employeeIdStr == null || shiftIdStr == null || selectedDate == null) {
            request.setAttribute("errorMessage", "Thông tin không đầy đủ. Vui lòng thử lại.");
            showAssignShiftForm(request, response);
            return;
        }
        
        try {
            int employeeId = Integer.parseInt(employeeIdStr);
            int shiftId = Integer.parseInt(shiftIdStr);
            
            //kiểm tra employee có tồn tại không
            User employee = userDAO.getUserById(employeeId);
            if (employee == null) {
                request.setAttribute("errorMessage", "Nhân viên không tồn tại.");
                getShiftsByDate(request, response);
                return;
            }
            
            //kiểm tra shiftslot có tồn tại không
            ShiftSlot shiftSlot = shiftSlotDAO.getShiftSlotById(shiftId);
            if (shiftSlot == null) {
                request.setAttribute("errorMessage", "Ca làm việc không tồn tại.");
                getShiftsByDate(request, response);
                return;
            }
            
            boolean success = employeeShiftDAO.managerRegisterShiftForEmployee(employeeId, shiftId);
            
            if (success) {
                request.setAttribute("successMessage", "Đã đăng ký ca làm việc thành công cho nhân viên " + employee.getName());
            } else {
                request.setAttribute("errorMessage", "Không thể đăng ký ca làm việc. Ca có thể đã đầy hoặc nhân viên đã đăng ký ca này.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
        }
        
        getShiftsByDate(request, response);
    }
} 