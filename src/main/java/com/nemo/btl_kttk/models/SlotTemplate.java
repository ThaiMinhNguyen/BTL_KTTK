package com.nemo.btl_kttk.models;

import java.io.Serializable;
import java.sql.Time;

public class SlotTemplate implements Serializable {
    private int id;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private int maxEmployee;
    
    public SlotTemplate() {
    }
    
    public SlotTemplate(int id, String dayOfWeek, Time startTime, Time endTime, int maxEmployee) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxEmployee = maxEmployee;
    }

    public SlotTemplate(String dayOfWeek, Time startTime, Time endTime, int maxEmployee) {
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

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
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