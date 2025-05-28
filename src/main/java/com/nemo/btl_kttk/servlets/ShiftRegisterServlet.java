package com.nemo.btl_kttk.servlets;

import com.nemo.btl_kttk.dao.EmployeeShiftDAO;
import com.nemo.btl_kttk.dao.ShiftSlotDAO;
import com.nemo.btl_kttk.models.EmployeeShift;
import com.nemo.btl_kttk.models.ShiftSlot;
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
import java.util.Date;
import java.util.List;

@WebServlet(name = "ShiftRegisterServlet", urlPatterns = {"/shift-register", "/shiftSlotDetail"})
public class ShiftRegisterServlet extends HttpServlet {
    
    private ShiftSlotDAO shiftSlotDAO;
    private EmployeeShiftDAO employeeShiftDAO;
    
    @Override
    public void init() throws ServletException {
        shiftSlotDAO = new ShiftSlotDAO();
        employeeShiftDAO = new EmployeeShiftDAO();
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
            handleShiftSlotDetail(request, response, user);
        } else {
            // Xử lý yêu cầu xem danh sách ca làm
            handleShiftListView(request, response, user);
        }
    }
    
    private void handleShiftSlotDetail(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
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
        
        // lấy available shiftslot cho ngày vừa chọn
        List<ShiftSlot> availableShifts = shiftSlotDAO.getAvailableShifts(weekStartDate);
        request.setAttribute("shiftSlots", availableShifts);
        
        // lấy ca làm đã đăng ký của user
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
        
        // Xử lý yêu cầu đăng ký/hủy đăng ký ca làm
        if ("register".equals(action)) {
            handleRegisterShift(request, response, user);
        } else if ("cancel".equals(action)) {
            handleCancelShift(request, response, user);
        }
        //gọi lại để cập nhật thay đổi
        handleShiftListView(request, response, user);
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