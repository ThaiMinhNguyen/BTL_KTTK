package com.nemo.btl_kttk.servlets;

import com.nemo.btl_kttk.dao.EmployeeShiftDAO;
import com.nemo.btl_kttk.dao.ShiftSlotDAO;
import com.nemo.btl_kttk.dao.TimeRecordDAO;
import com.nemo.btl_kttk.models.EmployeeShift;
import com.nemo.btl_kttk.models.ShiftSlot;
import com.nemo.btl_kttk.models.TimeRecord;
import com.nemo.btl_kttk.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@WebServlet(name = "ShiftRegisterServlet", urlPatterns = {"/shift-register", "/shiftSlotDetail"})
public class ShiftRegisterServlet extends HttpServlet {
    
    private ShiftSlotDAO shiftSlotDAO;
    private EmployeeShiftDAO employeeShiftDAO;
    private TimeRecordDAO timeRecordDAO;
    
    @Override
    public void init() throws ServletException {
        shiftSlotDAO = new ShiftSlotDAO();
        employeeShiftDAO = new EmployeeShiftDAO();
        timeRecordDAO = new TimeRecordDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());
        
        // Xử lý yêu cầu xem chi tiết ca làm
        if (path.contains("shiftSlotDetail")) {
            handleTimeRecordView(request, response, user);
        } else {
            // Xử lý yêu cầu xem danh sách ca làm
            handleShiftListView(request, response, user);
        }
    }
    
    private void handleTimeRecordView(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        // Lấy id của ShiftSlot từ url
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int shiftId = Integer.parseInt(idParam);
                ShiftSlot shiftSlot = shiftSlotDAO.getShiftSlotById(shiftId);
                
                if (shiftSlot != null) {
                    request.setAttribute("shiftSlot", shiftSlot);
                    
                    // kiểm tra xem employee đăng ký shift này chưa
                    List<EmployeeShift> employeeShifts = employeeShiftDAO.getEmployeeShiftsByUserId(user.getId());
                    boolean hasRegistered = false;
                    EmployeeShift userShift = null;
                    
                    for (EmployeeShift shift : employeeShifts) {
                        if (shift.getShiftSlot().getId() == shiftId) {
                            hasRegistered = true;
                            userShift = shift;
                            break;
                        }
                    }
                    
                    request.setAttribute("hasRegistered", hasRegistered);
                    request.setAttribute("userShift", userShift);
                    
                    request.getRequestDispatcher("gdChiTietCaLam.jsp").forward(request, response);
                } else {
                    request.setAttribute("errorMessage", "Không tìm thấy ca làm việc với ID: " + shiftId);
                    request.getRequestDispatcher("gdDangKyLich.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID ca làm việc không hợp lệ");
                request.getRequestDispatcher("gdDangKyLich.jsp").forward(request, response);
            }
        } else {
            // ko có id nào trong url thì quay lại trang đăng ký
            response.sendRedirect("shift-register");
        }
    }
    
    private void handleShiftListView(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        String weekDateParam = request.getParameter("weekStartDate");
        Date weekStartDate = null;
        
        if (weekDateParam != null && !weekDateParam.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                weekStartDate = sdf.parse(weekDateParam);
            } catch (ParseException e) {
                e.printStackTrace();
                weekStartDate = new Date(); 
            }
        } else {
            //Set ngày tìm kiếm là ngày hiện tại 
            weekStartDate = new Date(); 
        }
        
        // Get available shifts for the selected week
        List<ShiftSlot> availableShifts = shiftSlotDAO.getAvailableShifts(weekStartDate);
        request.setAttribute("shiftSlots", availableShifts);
        
        // Get employee's registered shifts
        List<EmployeeShift> employeeShifts = employeeShiftDAO.getEmployeeShiftsByUserId(user.getId());
        request.setAttribute("employeeShifts", employeeShifts);
        
        request.getRequestDispatcher("gdDangKyLich.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());
        
        // Xử lý yêu cầu xem chi tiết ca làm
        if (path.contains("shiftSlotDetail")) {
            handleTimeRecordSubmit(request, response, user);
            return;
        }
        
        // Xử lý yêu cầu đăng ký/hủy đăng ký ca làm
        if ("register".equals(action)) {
            handleRegisterShift(request, response, user);
        } else if ("cancel".equals(action)) {
            handleCancelShift(request, response, user);
        }
        
        // Instead of redirecting, forward to maintain request attributes
        handleShiftListView(request, response, user);
    }
    
    private void handleTimeRecordSubmit(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("record".equals(action)) {
            String shiftIdParam = request.getParameter("txtId");
            String startTimeParam = request.getParameter("txtStartTime");
            String endTimeParam = request.getParameter("txtEndTime");
            
            if (shiftIdParam != null && !shiftIdParam.isEmpty() && 
                startTimeParam != null && !startTimeParam.isEmpty() && 
                endTimeParam != null && !endTimeParam.isEmpty()) {
                
                try {
                    int shiftId = Integer.parseInt(shiftIdParam);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date startTime = dateFormat.parse(startTimeParam);
                    Date endTime = dateFormat.parse(endTimeParam);
                    
                    ShiftSlot shiftSlot = shiftSlotDAO.getShiftSlotById(shiftId);
                    
                    if (shiftSlot != null) {
                        // Check if user is registered for this shift
                        List<EmployeeShift> employeeShifts = employeeShiftDAO.getEmployeeShiftsByUserId(user.getId());
                        EmployeeShift userShift = null;
                        
                        for (EmployeeShift shift : employeeShifts) {
                            if (shift.getShiftSlot().getId() == shiftId) {
                                userShift = shift;
                                break;
                            }
                        }
                        
                        if (userShift != null) {
                            // User is registered for this shift, create a time record
                            TimeRecord timeRecord = new TimeRecord();
                            timeRecord.setEmployeeShift(userShift);
                            
                            // Convert Date to LocalDateTime
                            LocalDateTime startLDT = startTime.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                            
                            LocalDateTime endLDT = endTime.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                            
                            timeRecord.setActualStartTime(startLDT);
                            timeRecord.setActualEndTime(endLDT);
                            
                            // Save the time record
                            boolean success = timeRecordDAO.addTimeRecord(timeRecord);
                            
                            if (success) {
                                request.setAttribute("successMessage", "Thời gian làm việc đã được ghi nhận thành công");
                            } else {
                                request.setAttribute("errorMessage", "Không thể ghi nhận thời gian làm việc");
                            }
                        } else {
                            // User is not registered for this shift
                            request.setAttribute("errorMessage", "Bạn chưa đăng ký ca làm việc này");
                        }
                        
                        // Gọi lại hàm xem chi tiết để hiển thị thông tin cập nhật
                        request.setAttribute("shiftSlot", shiftSlot);
                        
                        // Cập nhật lại trạng thái đăng ký
                        boolean hasRegistered = userShift != null;
                        request.setAttribute("hasRegistered", hasRegistered);
                        request.setAttribute("userShift", userShift);
                        
                        request.getRequestDispatcher("gdChiTietCaLam.jsp").forward(request, response);
                    } else {
                        request.setAttribute("errorMessage", "Không tìm thấy ca làm việc");
                        response.sendRedirect("shift-register");
                    }
                    
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID ca làm việc không hợp lệ");
                    response.sendRedirect("shift-register");
                } catch (ParseException e) {
                    request.setAttribute("errorMessage", "Định dạng thời gian không hợp lệ");
                    response.sendRedirect("shift-register");
                }
            } else {
                request.setAttribute("errorMessage", "Vui lòng cung cấp đầy đủ thông tin");
                response.sendRedirect("shift-register");
            }
        } else {
            response.sendRedirect("shift-register");
        }
    }
    
    private void handleRegisterShift(HttpServletRequest request, HttpServletResponse response, User user) {
        String shiftIdParam = request.getParameter("shiftId");
        if (shiftIdParam != null && !shiftIdParam.isEmpty()) {
            try {
                int shiftId = Integer.parseInt(shiftIdParam);
                boolean success = employeeShiftDAO.registerShift(user.getId(), shiftId);
                
                if (success) {
                    request.setAttribute("successMessage", "Đăng ký ca làm việc thành công.");
                } else {
                    request.setAttribute("errorMessage", "Không thể đăng ký ca làm việc. Ca làm có thể đã đầy hoặc bạn đã đăng ký rồi.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID ca làm việc không hợp lệ.");
            }
        }
    }
    
    private void handleCancelShift(HttpServletRequest request, HttpServletResponse response, User user) {
        String employeeShiftIdParam = request.getParameter("employeeShiftId");
        if (employeeShiftIdParam != null && !employeeShiftIdParam.isEmpty()) {
            try {
                int employeeShiftId = Integer.parseInt(employeeShiftIdParam);
                
                // Kiểm tra xem shift này đã được user này đăng ký chưas
                EmployeeShift shift = employeeShiftDAO.getEmployeeShiftById(employeeShiftId);
                if (shift != null && shift.getEmployee().getId() == user.getId()) {
                    boolean success = employeeShiftDAO.cancelRegistration(employeeShiftId);
                    
                    if (success) {
                        request.setAttribute("successMessage", "Hủy đăng ký ca làm việc thành công.");
                    } else {
                        request.setAttribute("errorMessage", "Không thể hủy đăng ký ca làm việc. Có thể đã có thời gian làm việc được ghi nhận cho ca này.");
                    }
                } else {
                    request.setAttribute("errorMessage", "Bạn không có quyền hủy đăng ký ca làm việc này.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID đăng ký ca làm việc không hợp lệ.");
            }
        } else {
            request.setAttribute("errorMessage", "Không tìm thấy ID đăng ký ca làm việc.");
        }
    }
} 