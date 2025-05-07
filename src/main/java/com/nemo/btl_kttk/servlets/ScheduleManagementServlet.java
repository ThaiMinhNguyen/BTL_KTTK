package com.nemo.btl_kttk.servlets;

import com.nemo.btl_kttk.dao.ShiftSlotDAO;
import com.nemo.btl_kttk.dao.SlotTemplateDAO;
import com.nemo.btl_kttk.dao.WorkScheduleDAO;
import com.nemo.btl_kttk.dao.WorkScheduleSlotDAO;
import com.nemo.btl_kttk.models.ShiftSlot;
import com.nemo.btl_kttk.models.SlotTemplate;
import com.nemo.btl_kttk.models.User;
import com.nemo.btl_kttk.models.WorkSchedule;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ScheduleManagementServlet", urlPatterns = {"/schedule-management", "/publish-schedule", "/template-list", "/create-template"})
public class ScheduleManagementServlet extends HttpServlet {
    
    private SlotTemplateDAO slotTemplateDAO;
    private WorkScheduleDAO workScheduleDAO;
    private WorkScheduleSlotDAO workScheduleSlotDAO;
    private ShiftSlotDAO shiftSlotDAO;
    
    @Override
    public void init() throws ServletException {
        slotTemplateDAO = new SlotTemplateDAO();
        workScheduleDAO = new WorkScheduleDAO();
        workScheduleSlotDAO = new WorkScheduleSlotDAO();
        shiftSlotDAO = new ShiftSlotDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        
        switch (servletPath) {
            case "/schedule-management":
                showScheduleManagement(request, response);
                break;
            case "/publish-schedule":
                showPublishSchedule(request, response);
                break;
            case "/template-list":
                showTemplateList(request, response);
                break;
            case "/create-template":
                showCreateTemplate(request, response);
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
            case "create_schedule":
                createSchedule(request, response);
                break;
            case "publish_schedule":
                publishSchedule(request, response);
                break;
            case "delete_schedule":
                deleteSchedule(request, response);
                break;
            case "create_template":
                createNewTemplate(request, response);
                break;
            case "create_schedule_from_templates":
                createScheduleFromTemplates(request, response);
                break;
            default:
                response.sendRedirect("gdChinhQL.jsp");
                break;
        }
    }
    
    private void showScheduleManagement(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("gdQuanLyLich.jsp").forward(request, response);
    }
    
    private void showPublishSchedule(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<WorkSchedule> allSchedules = workScheduleDAO.getAllWorkSchedules();
        List<SlotTemplate> allTemplates = slotTemplateDAO.getAllTemplates();
        
        // Lấy thông tin lịch được chọn nếu có
        String scheduleIdStr = request.getParameter("scheduleId");
        if (scheduleIdStr != null && !scheduleIdStr.trim().isEmpty()) {
            try {
                int scheduleId = Integer.parseInt(scheduleIdStr);
                WorkSchedule selectedSchedule = workScheduleDAO.getWorkScheduleById(scheduleId);
                
                if (selectedSchedule != null) {
                    // Lấy danh sách SlotTemplate của WorkSchedule
                    List<SlotTemplate> scheduleTemplates = workScheduleSlotDAO.getSlotTemplatesByWorkScheduleId(scheduleId);
                    
                    request.setAttribute("selectedSchedule", selectedSchedule);
                    request.setAttribute("scheduleTemplates", scheduleTemplates);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID lịch không hợp lệ");
            }
        }
        
        request.setAttribute("schedules", allSchedules);
        request.setAttribute("templates", allTemplates);
        request.getRequestDispatcher("gdPublishSchedule.jsp").forward(request, response);
    }
    
    private void createSchedule(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("templateName");
        String[] selectedTemplates = request.getParameterValues("selectedTemplates");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            request.setAttribute("errorMessage", "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
            return;
        }
        
        if (name == null || name.trim().isEmpty() || selectedTemplates == null || selectedTemplates.length == 0) {
            request.setAttribute("errorMessage", "Vui lòng nhập tên lịch và chọn ít nhất một mẫu ca.");
            showPublishSchedule(request, response);
            return;
        }
        
        // Tạo WorkSchedule mới
        int workScheduleId = workScheduleDAO.createWorkSchedule(name, user.getId());
        
        if (workScheduleId == -1) {
            request.setAttribute("errorMessage", "Không thể tạo lịch làm việc. Vui lòng thử lại.");
            showPublishSchedule(request, response);
            return;
        }
        
        // Thêm các SlotTemplate vào WorkSchedule
        boolean success = true;
        for (String templateId : selectedTemplates) {
            try {
                int id = Integer.parseInt(templateId);
                if (!workScheduleSlotDAO.addSlotTemplateToWorkSchedule(workScheduleId, id)) {
                    success = false;
                    break;
                }
            } catch (NumberFormatException e) {
                success = false;
                break;
            }
        }
        
        if (!success) {
            workScheduleDAO.deleteWorkSchedule(workScheduleId);
            request.setAttribute("errorMessage", "Không thể thêm các mẫu ca vào lịch làm việc. Vui lòng thử lại.");
            showPublishSchedule(request, response);
            return;
        }
        
        request.setAttribute("successMessage", "Tạo lịch làm việc thành công!");
        showPublishSchedule(request, response);
    }
    
    private void publishSchedule(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String scheduleIdStr = request.getParameter("scheduleId");
        String weekStartDateStr = request.getParameter("weekStartDate");
        
        if (scheduleIdStr == null || weekStartDateStr == null) {
            request.setAttribute("errorMessage", "Thông tin không đầy đủ. Vui lòng thử lại.");
            showPublishSchedule(request, response);
            return;
        }
        
        try {
            int scheduleId = Integer.parseInt(scheduleIdStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date selectedDate = sdf.parse(weekStartDateStr);
            
            // Tính toán ngày đầu tuần (Thứ 2) từ ngày được chọn
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);
            
            // Lấy ngày trong tuần của ngày được chọn (trong Calendar: 1=Chủ nhật, 2=Thứ 2, ..., 7=Thứ 7)
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            
            // Tính số ngày cần trừ để về ngày Thứ 2
            int daysToSubtract;
            if (dayOfWeek == Calendar.SUNDAY) { // Chủ nhật
                daysToSubtract = 6; // Lùi 6 ngày để về Thứ 2 tuần trước
            } else {
                daysToSubtract = dayOfWeek - Calendar.MONDAY; // Lùi về Thứ 2 cùng tuần
            }
            
            // Điều chỉnh về ngày Thứ 2
            calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
            Date weekStartDate = calendar.getTime();
            
            // Hiển thị thông báo cho người dùng biết ngày bắt đầu tuần thực tế
            String weekStartDateFormatted = sdf.format(weekStartDate);
            
            // Lấy thông tin WorkSchedule
            WorkSchedule workSchedule = workScheduleDAO.getWorkScheduleById(scheduleId);
            if (workSchedule == null) {
                request.setAttribute("errorMessage", "Không tìm thấy lịch làm việc.");
                showPublishSchedule(request, response);
                return;
            }
            
            // Lấy danh sách SlotTemplate từ WorkSchedule
            List<SlotTemplate> templates = workScheduleSlotDAO.getSlotTemplatesByWorkScheduleId(scheduleId);
            if (templates.isEmpty()) {
                request.setAttribute("errorMessage", "Lịch làm việc không có mẫu ca nào.");
                showPublishSchedule(request, response);
                return;
            }
            
            // Tạo các ShiftSlot từ SlotTemplate và weekStartDate
            List<ShiftSlot> shiftSlots = createShiftSlotsFromTemplates(templates, weekStartDate, workSchedule.getCreatedBy());
            
            // Lưu các ShiftSlot vào database
            boolean saved = shiftSlotDAO.saveShiftSlots(shiftSlots);
            if (!saved) {
                request.setAttribute("errorMessage", "Không thể tạo ca làm việc. Vui lòng thử lại.");
                showPublishSchedule(request, response);
                return;
            }
            
            // Thêm thông báo về ngày bắt đầu tuần thực tế đã sử dụng
            request.setAttribute("successMessage", "Đã công bố lịch làm việc thành công cho tuần bắt đầu từ " + weekStartDateFormatted + "!");
            showScheduleManagement(request, response);
            
        } catch (NumberFormatException | ParseException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng thử lại.");
            showPublishSchedule(request, response);
        }
    }
    
    private void deleteSchedule(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String scheduleIdStr = request.getParameter("scheduleId");
        
        if (scheduleIdStr == null) {
            request.setAttribute("errorMessage", "Không tìm thấy ID lịch làm việc.");
            showScheduleManagement(request, response);
            return;
        }
        
        try {
            int scheduleId = Integer.parseInt(scheduleIdStr);
            boolean deleted = workScheduleDAO.deleteWorkSchedule(scheduleId);
            
            if (deleted) {
                request.setAttribute("successMessage", "Đã xóa lịch làm việc thành công!");
            } else {
                request.setAttribute("errorMessage", "Không thể xóa lịch làm việc. Vui lòng thử lại.");
            }
            
            showScheduleManagement(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID lịch làm việc không hợp lệ.");
            showScheduleManagement(request, response);
        }
    }
    
    /**
     * Tạo danh sách các ShiftSlot từ các SlotTemplate và ngày bắt đầu tuần
     * 
     * @param templates Danh sách các mẫu ca
     * @param weekStartDate Ngày bắt đầu tuần
     * @param createdBy Người tạo
     * @return Danh sách các ca làm việc
     */
    private List<ShiftSlot> createShiftSlotsFromTemplates(List<SlotTemplate> templates, Date weekStartDate, User createdBy) {
        List<ShiftSlot> shiftSlots = new ArrayList<>();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(weekStartDate);
        
        for (SlotTemplate template : templates) {
            int dayOfWeek = getDayOfWeekAsInt(template.getDayOfWeek());
            if (dayOfWeek == -1) continue;
            
            // Tính ngày cho ca làm việc trong tuần
            Calendar shiftCal = Calendar.getInstance();
            shiftCal.setTime(weekStartDate);
            shiftCal.add(Calendar.DATE, dayOfWeek - 1);  // Trừ 1 vì thứ 2 = 1, còn calendar thì CN = 1, T2 = 2
            Date shiftDate = shiftCal.getTime();
            
            // Tạo thời gian bắt đầu và kết thúc ca làm việc
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();
            
            startCal.setTime(shiftDate);
            Calendar templateStart = Calendar.getInstance();
            templateStart.setTime(template.getStartTime());
            startCal.set(Calendar.HOUR_OF_DAY, templateStart.get(Calendar.HOUR_OF_DAY));
            startCal.set(Calendar.MINUTE, templateStart.get(Calendar.MINUTE));
            
            endCal.setTime(shiftDate);
            Calendar templateEnd = Calendar.getInstance();
            templateEnd.setTime(template.getEndTime());
            endCal.set(Calendar.HOUR_OF_DAY, templateEnd.get(Calendar.HOUR_OF_DAY));
            endCal.set(Calendar.MINUTE, templateEnd.get(Calendar.MINUTE));
            
            // Tạo ShiftSlot
            ShiftSlot shiftSlot = new ShiftSlot();
            shiftSlot.setDayOfWeek(template.getDayOfWeek());
            shiftSlot.setStartTime(startCal.getTime());
            shiftSlot.setEndTime(endCal.getTime());
            shiftSlot.setWeekStartDate(weekStartDate);
            shiftSlot.setStatus("ACTIVE");
            shiftSlot.setMaxEmployee(template.getMaxEmployee());
            shiftSlot.setSlotTemplate(template);
            shiftSlot.setCreatedBy(createdBy);
            
            shiftSlots.add(shiftSlot);
        }
        
        return shiftSlots;
    }
    
    /**
     * Chuyển đổi tên ngày trong tuần thành số (thứ 2 = 1, thứ 3 = 2, ...)
     * 
     * @param dayOfWeek Tên ngày trong tuần (Thứ 2, Thứ 3, ...)
     * @return Số tương ứng hoặc -1 nếu không tìm thấy
     */
    private int getDayOfWeekAsInt(String dayOfWeek) {
        switch (dayOfWeek.trim().toLowerCase()) {
            case "thứ 2": 
            case "monday": 
            case "thứ hai": return 1;
            case "thứ 3": 
            case "tuesday": 
            case "thứ ba": return 2;
            case "thứ 4": 
            case "wednesday": 
            case "thứ tư": return 3;
            case "thứ 5": 
            case "thursday": 
            case "thứ năm": return 4;
            case "thứ 6": 
            case "friday": 
            case "thứ sáu": return 5;
            case "thứ 7": 
            case "saturday": 
            case "thứ bảy": return 6;
            case "chủ nhật": 
            case "sunday": 
            case "cn": return 7;
            default: return -1;
        }
    }
    
    /**
     * Hiển thị danh sách các templates có sẵn
     */
    private void showTemplateList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<SlotTemplate> templates = slotTemplateDAO.getAllTemplates();
        request.setAttribute("templates", templates);
        
        // Xử lý template được chọn nếu có
        String templateIdStr = request.getParameter("templateId");
        if (templateIdStr != null && !templateIdStr.isEmpty()) {
            try {
                int templateId = Integer.parseInt(templateIdStr);
                SlotTemplate selectedTemplate = slotTemplateDAO.getSlotTemplateById(templateId);
                if (selectedTemplate != null) {
                    request.setAttribute("selectedTemplate", selectedTemplate);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID template không hợp lệ");
            }
        }
        
        // Lưu giữ danh sách template đã chọn trong session
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<Integer> selectedTemplateIds = (List<Integer>) session.getAttribute("selectedTemplateIds");
        if (selectedTemplateIds == null) {
            selectedTemplateIds = new ArrayList<>();
            session.setAttribute("selectedTemplateIds", selectedTemplateIds);
        }
        
        // Xử lý thêm template được chọn vào danh sách
        String addTemplateIdStr = request.getParameter("addTemplateId");
        if (addTemplateIdStr != null && !addTemplateIdStr.isEmpty()) {
            try {
                int addTemplateId = Integer.parseInt(addTemplateIdStr);
                if (!selectedTemplateIds.contains(addTemplateId)) {
                    selectedTemplateIds.add(addTemplateId);
                    session.setAttribute("selectedTemplateIds", selectedTemplateIds);
                    request.setAttribute("successMessage", "Đã thêm template vào danh sách");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID template không hợp lệ");
            }
        }
        
        // Xử lý xóa template khỏi danh sách
        String removeTemplateIdStr = request.getParameter("removeTemplateId");
        if (removeTemplateIdStr != null && !removeTemplateIdStr.isEmpty()) {
            try {
                int removeTemplateId = Integer.parseInt(removeTemplateIdStr);
                selectedTemplateIds.remove(Integer.valueOf(removeTemplateId));
                session.setAttribute("selectedTemplateIds", selectedTemplateIds);
                request.setAttribute("successMessage", "Đã xóa template khỏi danh sách");
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID template không hợp lệ");
            }
        }
        
        // Lấy thông tin chi tiết các template đã chọn
        List<SlotTemplate> selectedTemplates = new ArrayList<>();
        for (Integer id : selectedTemplateIds) {
            SlotTemplate template = slotTemplateDAO.getSlotTemplateById(id);
            if (template != null) {
                selectedTemplates.add(template);
            }
        }
        request.setAttribute("selectedTemplates", selectedTemplates);
        
        request.getRequestDispatcher("gdDanhSachTemplate.jsp").forward(request, response);
    }
    
    /**
     * Hiển thị trang tạo template mới
     */
    private void showCreateTemplate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("gdTaoTemplate.jsp").forward(request, response);
    }
    
    /**
     * Xử lý việc tạo template mới
     */
    private void createNewTemplate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dayOfWeek = request.getParameter("dayOfWeek");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String maxEmployeeStr = request.getParameter("maxEmployee");
        
        // Kiểm tra dữ liệu đầu vào
        if (dayOfWeek == null || dayOfWeek.isEmpty() ||
            startTimeStr == null || startTimeStr.isEmpty() ||
            endTimeStr == null || endTimeStr.isEmpty() ||
            maxEmployeeStr == null || maxEmployeeStr.isEmpty()) {
            
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin.");
            showCreateTemplate(request, response);
            return;
        }
        
        try {
            // Chuyển đổi thời gian
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Time startTime = new Time(timeFormat.parse(startTimeStr).getTime());
            Time endTime = new Time(timeFormat.parse(endTimeStr).getTime());
            int maxEmployee = Integer.parseInt(maxEmployeeStr);
            
            // Tạo SlotTemplate mới
            SlotTemplate template = new SlotTemplate();
            template.setDayOfWeek(dayOfWeek); // Lưu ngày trong tuần bằng tiếng Anh
            template.setStartTime(startTime);
            template.setEndTime(endTime);
            template.setMaxEmployee(maxEmployee);
            
            // Lưu vào database
            boolean saved = slotTemplateDAO.createTemplate(template);
            
            if (saved) {
                request.setAttribute("successMessage", "Đã tạo template thành công");
                showTemplateList(request, response);
            } else {
                request.setAttribute("errorMessage", "Không thể tạo template. Vui lòng thử lại.");
                showCreateTemplate(request, response);
            }
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            showCreateTemplate(request, response);
        }
    }
    
    /**
     * Tạo lịch làm việc từ danh sách template đã chọn
     */
    private void createScheduleFromTemplates(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String scheduleName = request.getParameter("scheduleName");
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            request.setAttribute("errorMessage", "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
            request.getRequestDispatcher("gdDangnhap.jsp").forward(request, response);
            return;
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> selectedTemplateIds = (List<Integer>) session.getAttribute("selectedTemplateIds");
        
        if (scheduleName == null || scheduleName.trim().isEmpty() || selectedTemplateIds == null || selectedTemplateIds.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập tên lịch và chọn ít nhất một mẫu ca.");
            showTemplateList(request, response);
            return;
        }
        
        // Tạo WorkSchedule mới
        int workScheduleId = workScheduleDAO.createWorkSchedule(scheduleName, user.getId());
        
        if (workScheduleId == -1) {
            request.setAttribute("errorMessage", "Không thể tạo lịch làm việc. Vui lòng thử lại.");
            showTemplateList(request, response);
            return;
        }
        
        // Thêm các SlotTemplate vào WorkSchedule
        boolean success = true;
        for (Integer templateId : selectedTemplateIds) {
            if (!workScheduleSlotDAO.addSlotTemplateToWorkSchedule(workScheduleId, templateId)) {
                success = false;
                break;
            }
        }
        
        if (!success) {
            workScheduleDAO.deleteWorkSchedule(workScheduleId);
            request.setAttribute("errorMessage", "Không thể thêm các mẫu ca vào lịch làm việc. Vui lòng thử lại.");
            showTemplateList(request, response);
            return;
        }
        
        // Xóa danh sách template đã chọn trong session
        session.removeAttribute("selectedTemplateIds");
        
        request.setAttribute("successMessage", "Tạo lịch làm việc thành công!");
        showPublishSchedule(request, response);
    }
} 