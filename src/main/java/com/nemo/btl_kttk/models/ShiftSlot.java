package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.util.Date;

public class ShiftSlot implements Serializable {
    private int id;
    private String dayOfWeek;
    private Date startTime;
    private Date endTime;
    private Date weekStartDate;
    private String status;
    private int maxEmployee;
    private SlotTemplate slotTemplate;
    private User createdBy;
    
    public ShiftSlot() {
    }
    
    public ShiftSlot(int id, String dayOfWeek, Date startTime, Date endTime, Date weekStartDate, 
            String status, int maxEmployee, SlotTemplate slotTemplate, User createdBy) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekStartDate = weekStartDate;
        this.status = status;
        this.maxEmployee = maxEmployee;
        this.slotTemplate = slotTemplate;
        this.createdBy = createdBy;
    }
    
    public ShiftSlot(String dayOfWeek, Date startTime, Date endTime, Date weekStartDate, 
            String status, int maxEmployee, SlotTemplate slotTemplate, User createdBy) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekStartDate = weekStartDate;
        this.status = status;
        this.maxEmployee = maxEmployee;
        this.slotTemplate = slotTemplate;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(Date weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMaxEmployee() {
        return maxEmployee;
    }

    public void setMaxEmployee(int maxEmployee) {
        this.maxEmployee = maxEmployee;
    }

    public SlotTemplate getSlotTemplate() {
        return slotTemplate;
    }

    public void setSlotTemplate(SlotTemplate slotTemplate) {
        this.slotTemplate = slotTemplate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    @Override
    public String toString() {
        return "ShiftSlot{" + "id=" + id + ", dayOfWeek=" + dayOfWeek + ", startTime=" + startTime + 
                ", endTime=" + endTime + ", weekStartDate=" + weekStartDate + ", status=" + status + '}';
    }
} 