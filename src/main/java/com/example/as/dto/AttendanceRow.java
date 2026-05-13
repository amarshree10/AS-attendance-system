package com.example.as.dto;

import java.time.LocalDate;

public class AttendanceRow {
    private LocalDate workDate;

    private String startTime;
    private String endTime;
    private String breakStart;
    private String breakEnd;
    private String note;
    //勤務時間　自動精算
    private String workTime;
    //エラー判定
    private boolean error;

    private boolean holiday;

    //getter/setter


    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBreakStart() {
        return breakStart;
    }

    public void setBreakStart(String breakStart) {
        this.breakStart = breakStart;
    }

    public String getBreakEnd() {
        return breakEnd;
    }

    public void setBreakEnd(String breakEnd) {
        this.breakEnd = breakEnd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }
}
