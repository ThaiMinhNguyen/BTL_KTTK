package com.nemo.btl_kttk.servlets;

import com.nemo.btl_kttk.dao.EmployeeShiftDAO;
import com.nemo.btl_kttk.dao.PaymentDAO;
import com.nemo.btl_kttk.dao.TimeRecordDAO;
import com.nemo.btl_kttk.dao.UserDAO;
import com.nemo.btl_kttk.models.Payment;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/payment-management", "/process-payment"})
public class PaymentServlet extends HttpServlet {

    private EmployeeShiftDAO employeeShiftDAO;
    private TimeRecordDAO timeRecordDAO;
    private PaymentDAO paymentDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        employeeShiftDAO = new EmployeeShiftDAO();
        timeRecordDAO = new TimeRecordDAO();
        paymentDAO = new PaymentDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/payment-management":
                showPaymentManagement(request, response);
                break;
            case "/process-payment":
                showApprovalPage(request, response);
                break;
            default:
                response.sendRedirect("gdChinhQL.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("gdChinhQL.jsp");
            return;
        }

        switch (action) {
            case "approve_payment":
                approvePayment(request, response);
                break;
            case "confirm_payment":
                confirmPayment(request, response);
                break;
            default:
                response.sendRedirect("gdChinhQL.jsp");
                break;
        }
    }

    private void showPaymentManagement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || "EMPLOYEE".equals(user.getRole())) {
            response.sendRedirect("gdDangnhap.jsp");
            return;
        }

        // Lấy thông báo từ session (nếu có)
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");
        
        // Xóa thông báo khỏi session sau khi đã lấy
        session.removeAttribute("successMessage");
        session.removeAttribute("errorMessage");
        
        // Đặt thông báo vào request để hiển thị
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
        }
        
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }

        // lấy date từ request hoặc date hiện tại
        String weekStartDateStr = request.getParameter("weekStartDate");
        Date weekStartDate;

        if (weekStartDateStr != null && !weekStartDateStr.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                weekStartDate = dateFormat.parse(weekStartDateStr);
            } catch (ParseException e) {
                weekStartDate = new Date();
            }
        } else {
            weekStartDate = new Date();
        }

        // Chỉnh về ngày đầu tuần
        weekStartDate = adjustToMondayOfWeek(weekStartDate);

        //lấy tất cả payment của tuần
        List<Payment> payments = paymentDAO.getPaymentsByWeek(weekStartDate);

        request.setAttribute("weekStartDate", weekStartDate);
        request.setAttribute("payments", payments);

        
        request.getRequestDispatcher("gdQuanlyChamcong.jsp").forward(request, response);
    }

    private void showApprovalPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || "EMPLOYEE".equals(user.getRole())) {
            response.sendRedirect("gdDangnhap.jsp");
            return;
        }
         
        String paymentIdStr = request.getParameter("paymentId");

        if (paymentIdStr != null) {
            try {
                int paymentId = Integer.parseInt(paymentIdStr);

                //lấy payment
                Payment payment = paymentDAO.getPaymentById(paymentId);
                
                if (payment != null) {
                    
                    User employee = payment.getEmployee();
                    
                    Date weekStartDate = payment.getWeekStartDate();
                    
                    List<TimeRecord> timeRecords = timeRecordDAO.getTimeRecordsByPaymentId(paymentId);
                    
                    request.setAttribute("employee", employee);
                    request.setAttribute("weekStartDate", weekStartDate);
                    request.setAttribute("timeRecords", timeRecords);
                    request.setAttribute("totalHours", payment.getTotalHour());
                    request.setAttribute("amount", payment.getAmount());
                    request.setAttribute("payment", payment);
                    
                    request.getRequestDispatcher("gdPheDuyetChamcong.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
            }
        }

        response.sendRedirect("payment-management");
    }

    private void approvePayment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || "EMPLOYEE".equals(user.getRole())) {
            response.sendRedirect("gdDangnhap.jsp");
            return;
        }

        //Lấy paymentId
        String paymentIdStr = request.getParameter("paymentId");
        String actualTotalPaymentStr = request.getParameter("actualTotalPayment");

        try {
            int paymentId = Integer.parseInt(paymentIdStr);
            double actualTotalPayment = Double.parseDouble(actualTotalPaymentStr);
            
            //Lấy payment
            Payment payment = paymentDAO.getPaymentById(paymentId);
            
            if (payment != null) {
                // Kiểm tra xem tuần đã kết thúc chưa
                boolean isWeekEnded = isWeekEnded(payment.getWeekStartDate());
                
                if (!isWeekEnded) {
                    request.setAttribute("errorMessage", "Không thể phê duyệt thanh toán khi tuần chấm công chưa kết thúc!");
                    
                    User employee = payment.getEmployee();
                    Date weekStartDate = payment.getWeekStartDate();
                    List<TimeRecord> timeRecords = timeRecordDAO.getTimeRecordsByPaymentId(paymentId);
                    
                    request.setAttribute("employee", employee);
                    request.setAttribute("weekStartDate", weekStartDate);
                    request.setAttribute("timeRecords", timeRecords);
                    request.setAttribute("totalHours", payment.getTotalHour());
                    request.setAttribute("amount", payment.getAmount());
                    request.setAttribute("payment", payment);
                    
                    request.getRequestDispatcher("gdPheDuyetChamcong.jsp").forward(request, response);
                    return;
                }
                
                //chuyển status thành APPROVED và cập nhật số tiền thực tế và paymentDate
                boolean success = paymentDAO.approvePayment(paymentId, user.getId(), new Date(), actualTotalPayment);
                
                if (success) {
                    // Lưu thông báo thành công vào session
                    session.setAttribute("successMessage", "Thanh toán đã được phê duyệt thành công!");
                    
                    // Chuyển hướng về trang quản lý chấm công
                    response.sendRedirect("payment-management");
                    return;
                } else {
                    session.setAttribute("errorMessage", "Có lỗi xảy ra khi phê duyệt thanh toán.");
                }
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
        }

        response.sendRedirect("payment-management");
    }
    
    private void confirmPayment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || "EMPLOYEE".equals(user.getRole())) {
            response.sendRedirect("gdDangnhap.jsp");
            return;
        }

        //Lấy paymentId
        String paymentIdStr = request.getParameter("paymentId");

        try {
            int paymentId = Integer.parseInt(paymentIdStr);
            
            //Lấy payment
            Payment payment = paymentDAO.getPaymentById(paymentId);
            
            boolean success = false;
            if (payment != null && "APPROVED".equals(payment.getStatus())) {
                //chuyển status thành PAID và cập nhật ngày thanh toán
                success = paymentDAO.confirmPayment(paymentId, new Date());
            }

            if (success) {
                // Lưu thông báo thành công vào session
                session.setAttribute("successMessage", "Đã xác nhận thanh toán cho nhân viên thành công!");
                
                // Chuyển hướng về trang quản lý chấm công
                response.sendRedirect("payment-management");
                return;
            } else {
                session.setAttribute("errorMessage", "Có lỗi xảy ra khi xác nhận thanh toán.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
        }

        response.sendRedirect("payment-management");
    }

    //Hàm kiểm tra xem tuần có weekStartDate đã kết thúc chưa
    private boolean isWeekEnded(Date weekStartDate) {
        if (weekStartDate == null) {
            return false;
        }
        
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
            return true;
        }
        
        // Hoặc nếu ngày hôm nay nằm trong tuần tiếp theo
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int startWeek = startLocalDate.get(weekFields.weekOfWeekBasedYear());
        int startYear = startLocalDate.get(weekFields.weekBasedYear());
        int todayWeek = today.get(weekFields.weekOfWeekBasedYear());
        int todayYear = today.get(weekFields.weekBasedYear());
        
        return todayYear > startYear || (todayYear == startYear && todayWeek > startWeek);
    }

    //Hàm chuyển ngày trong tuần thành ngày đầu tuần
    private Date adjustToMondayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // lấy ngày trong tuần (trong Calendar thì Sunday là 1)
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);

        //Trừ đi để chuyển về ngày đầu tuần
        int daysToSubtract;
        if (dayOfWeek == java.util.Calendar.SUNDAY) {
            daysToSubtract = 6; 
        } else {
            daysToSubtract = dayOfWeek - java.util.Calendar.MONDAY; 
        }

        //chuyển thành ngày đầu tuần
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -daysToSubtract);

        return calendar.getTime();
    }
}
