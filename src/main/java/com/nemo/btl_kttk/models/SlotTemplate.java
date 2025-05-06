package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.util.Date;

public class SlotTemplate implements Serializable {
    private int id;
    private String dayOfWeek;
    private Date startTime;
    private Date endTime;
    private int maxEmployee;
    
    public SlotTemplate() {
    }
    
    public SlotTemplate(int id, String dayOfWeek, Date startTime, Date endTime, int maxEmployee) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxEmployee = maxEmployee;
    }

    public SlotTemplate(String dayOfWeek, Date startTime, Date endTime, int maxEmployee) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxEmployee = maxEmployee;
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

    public int getMaxEmployee() {
        return maxEmployee;
    }

    public void setMaxEmployee(int maxEmployee) {
        this.maxEmployee = maxEmployee;
    }
    
    @Override
    public String toString() {
        return "SlotTemplate{" + "id=" + id + ", dayOfWeek=" + dayOfWeek + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }
} 