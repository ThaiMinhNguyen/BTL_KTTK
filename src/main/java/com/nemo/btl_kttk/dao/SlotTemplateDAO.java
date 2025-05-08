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
    
} 