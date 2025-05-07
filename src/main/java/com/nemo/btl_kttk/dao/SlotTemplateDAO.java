package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.SlotTemplate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class SlotTemplateDAO extends DAO {
    
    public SlotTemplateDAO() {
        super();
    }
    
    public SlotTemplate getSlotTemplateById(int id) {
        String sql = "SELECT * FROM SlotTemplate WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SlotTemplate slotTemplate = new SlotTemplate();
                slotTemplate.setId(rs.getInt("id"));
                slotTemplate.setDayOfWeek(rs.getString("dayOfWeek"));
                slotTemplate.setStartTime(rs.getTime("startTime"));
                slotTemplate.setEndTime(rs.getTime("endTime"));
                slotTemplate.setMaxEmployee(rs.getInt("maxEmployee"));
                return slotTemplate;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy tất cả template thuộc về một lịch làm việc
     * 
     * @param workScheduleId ID của lịch làm việc
     * @return Danh sách các template
     */
    public List<SlotTemplate> getTemplatesByTemplateId(int workScheduleId) {
        List<SlotTemplate> templates = new ArrayList<>();
        String sql = "SELECT * FROM SlotTemplate WHERE workScheduleId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, workScheduleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SlotTemplate slotTemplate = new SlotTemplate();
                slotTemplate.setId(rs.getInt("id"));
                slotTemplate.setDayOfWeek(rs.getString("dayOfWeek"));
                slotTemplate.setStartTime(rs.getTime("startTime"));
                slotTemplate.setEndTime(rs.getTime("endTime"));
                slotTemplate.setMaxEmployee(rs.getInt("maxEmployee"));
                templates.add(slotTemplate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templates;
    }
    
    public List<SlotTemplate> getAllTemplates() {
        List<SlotTemplate> slotTemplates = new ArrayList<>();
        String sql = "SELECT * FROM SlotTemplate";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                SlotTemplate slotTemplate = new SlotTemplate();
                slotTemplate.setId(rs.getInt("id"));
                slotTemplate.setDayOfWeek(rs.getString("dayOfWeek"));
                slotTemplate.setStartTime(rs.getTime("startTime"));
                slotTemplate.setEndTime(rs.getTime("endTime"));
                slotTemplate.setMaxEmployee(rs.getInt("maxEmployee"));
                slotTemplates.add(slotTemplate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slotTemplates;
    }
    
    public boolean createTemplate(SlotTemplate slotTemplate) {
        String sql = "INSERT INTO SlotTemplate (dayOfWeek, startTime, endTime, maxEmployee) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, slotTemplate.getDayOfWeek());
            ps.setTime(2, slotTemplate.getStartTime());
            ps.setTime(3, slotTemplate.getEndTime());
            ps.setInt(4, slotTemplate.getMaxEmployee());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }
            
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                slotTemplate.setId(generatedKeys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSlotTemplate(SlotTemplate slotTemplate) {
        String sql = "UPDATE SlotTemplate SET dayOfWeek = ?, startTime = ?, endTime = ?, maxEmployee = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, slotTemplate.getDayOfWeek());
            ps.setTime(2, slotTemplate.getStartTime());
            ps.setTime(3, slotTemplate.getEndTime());
            ps.setInt(4, slotTemplate.getMaxEmployee());
            ps.setInt(5, slotTemplate.getId());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteSlotTemplate(int id) {
        String sql = "DELETE FROM SlotTemplate WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 