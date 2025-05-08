package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.SlotTemplate;
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
    
} 