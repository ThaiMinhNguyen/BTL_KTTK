package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Payment implements Serializable {
    private int id;
    private User employee;
    private Date weekStartDate;
    private Date paymentDate;
    private double totalHour;
    private double amount;
    private double bonus;
    private String status;
    private User processedBy;
    private List<TimeRecord> timeRecords;
    
    public Payment() {
    }
    
    public Payment(int id, User employee, Date weekStartDate, Date paymentDate, double totalHour, double amount, double bonus, String status, User processedBy) {
        this.id = id;
        this.employee = employee;
        this.weekStartDate = weekStartDate;
        this.paymentDate = paymentDate;
        this.totalHour = totalHour;
        this.amount = amount;
        this.bonus = bonus;
        this.status = status;
        this.processedBy = processedBy;
    }
    
    public Payment(User employee, Date weekStartDate, Date paymentDate, double totalHour, double amount, double bonus, String status, User processedBy) {
        this.employee = employee;
        this.weekStartDate = weekStartDate;
        this.paymentDate = paymentDate;
        this.totalHour = totalHour;
        this.amount = amount;
        this.bonus = bonus;
        this.status = status;
        this.processedBy = processedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public Date getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(Date weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getTotalHour() {
        return totalHour;
    }

    public void setTotalHour(double totalHour) {
        this.totalHour = totalHour;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }
    
    public List<TimeRecord> getTimeRecords() {
        return timeRecords;
    }
    
    public void setTimeRecords(List<TimeRecord> timeRecords) {
        this.timeRecords = timeRecords;
    }
    
    @Override
    public String toString() {
        return "Payment{" + "id=" + id + ", employee=" + employee.getUsername() + ", weekStartDate=" + weekStartDate + 
                ", paymentDate=" + paymentDate + ", totalHour=" + totalHour + ", amount=" + amount + 
                ", bonus=" + bonus + ", status=" + status + ", processedBy=" + (processedBy != null ? processedBy.getUsername() : "null") + '}';
    }
} 