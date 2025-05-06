package com.nemo.btl_kttk.models;

import java.io.Serializable;

public class WorkScheduleSlot implements Serializable {
    private int id;
    private WorkSchedule workSchedule;
    private SlotTemplate slotTemplate;
    
    public WorkScheduleSlot() {
    }
    
    public WorkScheduleSlot(int id, WorkSchedule workSchedule, SlotTemplate slotTemplate) {
        this.id = id;
        this.workSchedule = workSchedule;
        this.slotTemplate = slotTemplate;
    }
    
    public WorkScheduleSlot(WorkSchedule workSchedule, SlotTemplate slotTemplate) {
        this.workSchedule = workSchedule;
        this.slotTemplate = slotTemplate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WorkSchedule getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(WorkSchedule workSchedule) {
        this.workSchedule = workSchedule;
    }

    public SlotTemplate getSlotTemplate() {
        return slotTemplate;
    }

    public void setSlotTemplate(SlotTemplate slotTemplate) {
        this.slotTemplate = slotTemplate;
    }
    
    @Override
    public String toString() {
        return "WorkScheduleSlot{" + "id=" + id + ", workSchedule=" + workSchedule.getId() + ", slotTemplate=" + slotTemplate.getId() + '}';
    }
} 