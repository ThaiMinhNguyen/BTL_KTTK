package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.ShiftSlot;
import com.nemo.btl_kttk.models.SlotTemplate;
import com.nemo.btl_kttk.models.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShiftSlotDAO extends DAO {

    private UserDAO userDAO;
    private SlotTemplateDAO slotTemplateDAO;

    public ShiftSlotDAO() {
        super();
        userDAO = new UserDAO();
        slotTemplateDAO = new SlotTemplateDAO();
    }

    public List<ShiftSlot> getAvailableShifts(Date selectedDate) {
        List<ShiftSlot> shiftSlots = new ArrayList<>();

        // Chuyển đổi selectedDate thành java.sql.Date để so sánh chính xác ngày
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(selectedDate);

        // Đặt thời gian về đầu ngày để so sánh chính xác
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);

        Date adjustedDate = calendar.getTime();

        // Format ngày của selectedDate thành chuỗi ngày tháng để so sánh
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String selectedDateStr = dateFormat.format(adjustedDate);

        // Truy vấn để lấy ca làm việc có startTime trong ngày được chọn
        String sql = "SELECT * FROM ShiftSlot WHERE DATE(startTime) = ? AND status = 'ACTIVE'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, selectedDateStr);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ShiftSlot shiftSlot = new ShiftSlot();
                shiftSlot.setId(rs.getInt("id"));
                shiftSlot.setDayOfWeek(rs.getString("dayOfWeek"));
                shiftSlot.setStartTime(rs.getTimestamp("startTime"));
                shiftSlot.setEndTime(rs.getTimestamp("endTime"));
                shiftSlot.setWeekStartDate(rs.getDate("weekStartDate"));
                shiftSlot.setStatus(rs.getString("status"));
                shiftSlot.setMaxEmployee(rs.getInt("maxEmployee"));

                // load slot template
                SlotTemplate slotTemplate = slotTemplateDAO.getSlotTemplateById(rs.getInt("tblSlotTemplateId"));
                shiftSlot.setSlotTemplate(slotTemplate);

                // load created by user
                User createdBy = userDAO.getUserById(rs.getInt("tblCreatedById"));
                shiftSlot.setCreatedBy(createdBy);

                shiftSlots.add(shiftSlot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shiftSlots;
    }

    public ShiftSlot getShiftSlotById(int id) {
        String sql = "SELECT * FROM ShiftSlot WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ShiftSlot shiftSlot = new ShiftSlot();
                shiftSlot.setId(rs.getInt("id"));
                shiftSlot.setDayOfWeek(rs.getString("dayOfWeek"));
                shiftSlot.setStartTime(rs.getTimestamp("startTime"));
                shiftSlot.setEndTime(rs.getTimestamp("endTime"));
                shiftSlot.setWeekStartDate(rs.getDate("weekStartDate"));
                shiftSlot.setStatus(rs.getString("status"));
                shiftSlot.setMaxEmployee(rs.getInt("maxEmployee"));

                // load slot template
                SlotTemplate slotTemplate = slotTemplateDAO.getSlotTemplateById(rs.getInt("tblSlotTemplateId"));
                shiftSlot.setSlotTemplate(slotTemplate);

                // load created by user
                User createdBy = userDAO.getUserById(rs.getInt("tblCreatedById"));
                shiftSlot.setCreatedBy(createdBy);

                return shiftSlot;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isShiftAvailable(int shiftId) {
        String sql = "SELECT ss.maxEmployee, COUNT(es.id) as registered " +
                "FROM ShiftSlot ss " +
                "LEFT JOIN EmployeeShift es ON ss.id = es.tblShiftSlotId " +
                "WHERE ss.id = ? AND ss.status = 'ACTIVE' " +
                "GROUP BY ss.id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, shiftId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxEmployee = rs.getInt("maxEmployee");
                int registered = rs.getInt("registered");
                return registered < maxEmployee;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveShiftSlot(ShiftSlot shiftSlot) {
        String sql = "INSERT INTO ShiftSlot (dayOfWeek, startTime, endTime, weekStartDate, status, maxEmployee, tblSlotTemplateId, tblCreatedById) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, shiftSlot.getDayOfWeek());
            ps.setTimestamp(2, new Timestamp(shiftSlot.getStartTime().getTime()));
            ps.setTimestamp(3, new Timestamp(shiftSlot.getEndTime().getTime()));
            ps.setDate(4, new java.sql.Date(shiftSlot.getWeekStartDate().getTime()));
            ps.setString(5, shiftSlot.getStatus());
            ps.setInt(6, shiftSlot.getMaxEmployee());
            ps.setInt(7, shiftSlot.getSlotTemplate().getId());
            ps.setInt(8, shiftSlot.getCreatedBy().getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                shiftSlot.setId(generatedKeys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean saveShiftSlots(List<ShiftSlot> shiftSlots) {
        if (shiftSlots == null || shiftSlots.isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO ShiftSlot (dayOfWeek, startTime, endTime, weekStartDate, status, " +
                "maxEmployee, tblSlotTemplateId, tblCreatedById) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (ShiftSlot shiftSlot : shiftSlots) {
                ps.setString(1, shiftSlot.getDayOfWeek());
                ps.setTimestamp(2, new Timestamp(shiftSlot.getStartTime().getTime()));
                ps.setTimestamp(3, new Timestamp(shiftSlot.getEndTime().getTime()));
                ps.setDate(4, new java.sql.Date(shiftSlot.getWeekStartDate().getTime()));
                ps.setString(5, shiftSlot.getStatus());
                ps.setInt(6, shiftSlot.getMaxEmployee());

                // Set slot template ID
                if (shiftSlot.getSlotTemplate() != null) {
                    ps.setInt(7, shiftSlot.getSlotTemplate().getId());
                } else {
                    ps.setNull(7, java.sql.Types.INTEGER);
                }

                // Set created by user ID
                if (shiftSlot.getCreatedBy() != null) {
                    ps.setInt(8, shiftSlot.getCreatedBy().getId());
                } else {
                    ps.setNull(8, java.sql.Types.INTEGER);
                }

                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            connection.commit();

            // Kiểm tra kết quả và gán ID cho các đối tượng ShiftSlot nếu thành công
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int index = 0;
            while (generatedKeys.next() && index < shiftSlots.size()) {
                shiftSlots.get(index).setId(generatedKeys.getInt(1));
                index++;
            }

            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}