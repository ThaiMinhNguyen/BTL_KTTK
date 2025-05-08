package com.nemo.btl_kttk.dao;

import com.nemo.btl_kttk.models.EmployeeShift;
import com.nemo.btl_kttk.models.TimeRecord;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeRecordDAO extends DAO {

    private EmployeeShiftDAO employeeShiftDAO;
    private PaymentDAO paymentDAO;
    private UserDAO userDAO;

    public TimeRecordDAO() {
        super();
        employeeShiftDAO = new EmployeeShiftDAO();
        paymentDAO = new PaymentDAO();
        userDAO = new UserDAO();
    }

    public List<TimeRecord> getTimeRecordsByPaymentId(int paymentId) {
        List<TimeRecord> timeRecords = new ArrayList<>();
        String sql = "SELECT * FROM TimeRecord WHERE tblPaymentId = ? ORDER BY actualStartTime ASC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, paymentId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TimeRecord timeRecord = new TimeRecord();
                timeRecord.setId(rs.getInt("id"));

                Timestamp startTimestamp = rs.getTimestamp("actualStartTime");
                if (startTimestamp != null) {
                    timeRecord.setActualStartTime(startTimestamp.toLocalDateTime());
                }

                Timestamp endTimestamp = rs.getTimestamp("actualEndTime");
                if (endTimestamp != null) {
                    timeRecord.setActualEndTime(endTimestamp.toLocalDateTime());
                }

                // Lấy thông tin EmployeeShift
                int employeeShiftId = rs.getInt("tblEmployeeShiftId");
                EmployeeShift employeeShift = employeeShiftDAO.getEmployeeShiftById(employeeShiftId);
                timeRecord.setEmployeeShift(employeeShift);

                timeRecords.add(timeRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeRecords;
    }

}
