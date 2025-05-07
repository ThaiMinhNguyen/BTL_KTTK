package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.SlotTemplate;
import com.nemo.btl_kttk.models.WorkSchedule;
import com.nemo.btl_kttk.models.WorkScheduleSlot;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkScheduleSlotDAO extends DAO {
    
    private SlotTemplateDAO slotTemplateDAO;
    private WorkScheduleDAO workScheduleDAO;
    
    public WorkScheduleSlotDAO() {
        super();
        slotTemplateDAO = new SlotTemplateDAO();
        workScheduleDAO = new WorkScheduleDAO();
    }
    
    /**
     * Lấy danh sách các SlotTemplate thuộc về một WorkSchedule
     * 
     * @param workScheduleId ID của lịch làm việc
     * @return Danh sách các SlotTemplate
     */
    public List<SlotTemplate> getSlotTemplatesByWorkScheduleId(int workScheduleId) {
        List<SlotTemplate> slotTemplates = new ArrayList<>();
        String sql = "SELECT tblSlotTemplateId FROM WorkScheduleSlot WHERE tblWorkScheduleId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, workScheduleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int slotTemplateId = rs.getInt("tblSlotTemplateId");
                SlotTemplate slotTemplate = slotTemplateDAO.getSlotTemplateById(slotTemplateId);
                if (slotTemplate != null) {
                    slotTemplates.add(slotTemplate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slotTemplates;
    }
    
    /**
     * Lấy danh sách các WorkScheduleSlot thuộc về một WorkSchedule
     * 
     * @param workScheduleId ID của lịch làm việc
     * @return Danh sách các WorkScheduleSlot
     */
    public List<WorkScheduleSlot> getWorkScheduleSlotsByWorkScheduleId(int workScheduleId) {
        List<WorkScheduleSlot> workScheduleSlots = new ArrayList<>();
        String sql = "SELECT * FROM WorkScheduleSlot WHERE tblWorkScheduleId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, workScheduleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                WorkScheduleSlot workScheduleSlot = new WorkScheduleSlot();
                workScheduleSlot.setId(rs.getInt("id"));
                
                int scheduleId = rs.getInt("tblWorkScheduleId");
                WorkSchedule workSchedule = workScheduleDAO.getWorkScheduleById(scheduleId);
                workScheduleSlot.setWorkSchedule(workSchedule);
                
                int slotTemplateId = rs.getInt("tblSlotTemplateId");
                SlotTemplate slotTemplate = slotTemplateDAO.getSlotTemplateById(slotTemplateId);
                workScheduleSlot.setSlotTemplate(slotTemplate);
                
                workScheduleSlots.add(workScheduleSlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workScheduleSlots;
    }
    
    /**
     * Thêm một SlotTemplate vào WorkSchedule
     * 
     * @param workScheduleId ID của lịch làm việc
     * @param slotTemplateId ID của mẫu ca làm việc
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addSlotTemplateToWorkSchedule(int workScheduleId, int slotTemplateId) {
        String sql = "INSERT INTO WorkScheduleSlot (tblWorkScheduleId, tblSlotTemplateId) VALUES (?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, workScheduleId);
            ps.setInt(2, slotTemplateId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa một SlotTemplate khỏi WorkSchedule
     * 
     * @param workScheduleId ID của lịch làm việc
     * @param slotTemplateId ID của mẫu ca làm việc
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean removeSlotTemplateFromWorkSchedule(int workScheduleId, int slotTemplateId) {
        String sql = "DELETE FROM WorkScheduleSlot WHERE tblWorkScheduleId = ? AND tblSlotTemplateId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, workScheduleId);
            ps.setInt(2, slotTemplateId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 