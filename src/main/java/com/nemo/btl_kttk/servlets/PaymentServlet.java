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
import java.util.Date;
import java.util.List;

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
                showTimekeepingManagement(request, response);
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
            default:
                response.sendRedirect("gdChinhQL.jsp");
                break;
        }
    }

    private void showTimekeepingManagement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("gdDangnhap.jsp");
            return;
        }

        // Get date from request or use current date
        String weekStartDateStr = request.getParameter("weekStartDate");
        Date weekStartDate;

        if (weekStartDateStr != null && !weekStartDateStr.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                weekStartDate = dateFormat.parse(weekStartDateStr);
            } catch (ParseException e) {
                weekStartDate = new Date(); // Use current date if parsing fails
            }
        } else {
            weekStartDate = new Date();
        }

        // Adjust to the Monday of current week if needed
        weekStartDate = adjustToMondayOfWeek(weekStartDate);

        // Get all payments for the selected week
        List<Payment> payments = paymentDAO.getPaymentsByWeek(weekStartDate);

        // Set attributes for the JSP
        request.setAttribute("weekStartDate", weekStartDate);
        request.setAttribute("payments", payments);

        // Forward to JSP
        request.getRequestDispatcher("gdQuanlyChamcong.jsp").forward(request, response);
    }

    private void showApprovalPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("gdDangnhap.jsp");
            return;
        }

        // Get employee from request
        String employeeIdStr = request.getParameter("employeeId");
        String weekStartDateStr = request.getParameter("weekStartDate");

        if (employeeIdStr != null && weekStartDateStr != null) {
            try {
                int employeeId = Integer.parseInt(employeeIdStr);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date weekStartDate = dateFormat.parse(weekStartDateStr);

                // Get user
                User employee = userDAO.getUserById(employeeId);

                // Get time records for the employee for the selected week
                List<TimeRecord> timeRecords = timeRecordDAO.getTimeRecordsByUser(employeeId, weekStartDate);

                // Calculate total hours and amount
                double totalHours = 0;
                double amount = 0;

                

                // Check if payment already exists
                Payment existingPayment = paymentDAO.getUserPaymentByWeek(employeeId, weekStartDate);
                if (existingPayment != null) {
                    totalHours = existingPayment.getTotalHour();
                    amount = existingPayment.getAmount();
                    request.setAttribute("payment", existingPayment);
                }
                
                // Set attributes for the JSP
                request.setAttribute("employee", employee);
                request.setAttribute("weekStartDate", weekStartDate);
                request.setAttribute("timeRecords", timeRecords);
                request.setAttribute("totalHours", totalHours);
                request.setAttribute("amount", amount);
                
                // Forward to JSP
                request.getRequestDispatcher("gdPheDuyetChamcong.jsp").forward(request, response);
                return;
            } catch (NumberFormatException | ParseException e) {
                // Do nothing, will redirect to timekeeping management
            }
        }

        // Redirect back if parameters are missing or invalid
        response.sendRedirect("payment-management");
    }

    private void approvePayment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("gdDangnhap.jsp");
            return;
        }

        // Get parameters
        String employeeIdStr = request.getParameter("employeeId");
        String weekStartDateStr = request.getParameter("weekStartDate");
        String totalHoursStr = request.getParameter("totalHours");
        String amountStr = request.getParameter("amount");

        try {
            int employeeId = Integer.parseInt(employeeIdStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date weekStartDate = dateFormat.parse(weekStartDateStr);
            double totalHours = Double.parseDouble(totalHoursStr);
            double amount = Double.parseDouble(amountStr);

            // Get employee
            User employee = userDAO.getUserById(employeeId);

            // Check if payment already exists
            Payment existingPayment = paymentDAO.getUserPaymentByWeek(employeeId, weekStartDate);

            boolean success;
            if (existingPayment != null) {
                //Set status thành PAID
                success = paymentDAO.processPayment(existingPayment.getId(), user.getId(), new Date());
            } else {
                // Create new payment
                Payment payment = new Payment();
                payment.setEmployee(employee);
                payment.setWeekStartDate(weekStartDate);
                payment.setPaymentDate(new Date());
                payment.setTotalHour(totalHours);
                payment.setAmount(amount);
                payment.setStatus("PAID");
                payment.setProcessedBy(user);

                success = paymentDAO.createPayment(payment);
            }

            if (success) {
                request.setAttribute("successMessage", "Thanh toán đã được phê duyệt thành công!");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi phê duyệt thanh toán.");
            }

        } catch (NumberFormatException | ParseException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
        }

        // Redirect back to timekeeping management
        showTimekeepingManagement(request, response);
    }

    /**
     * Adjust a date to the Monday of its week
     *
     * @param date The date to adjust
     * @return The Monday of the week
     */
    private Date adjustToMondayOfWeek(Date date) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);

        // Get day of week (in Java Calendar: 1=Sunday, 2=Monday, ..., 7=Saturday)
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);

        // Calculate days to subtract to get to Monday
        int daysToSubtract;
        if (dayOfWeek == java.util.Calendar.SUNDAY) { // Sunday
            daysToSubtract = 6; // Subtract 6 days to get to previous Monday
        } else {
            daysToSubtract = dayOfWeek - java.util.Calendar.MONDAY; // Subtract to get to Monday of same week
        }

        // Adjust date to Monday
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -daysToSubtract);

        return calendar.getTime();
    }
}
