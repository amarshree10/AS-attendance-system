package com.example.as.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendances", uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "date"})) //同じユーザーの同日重複防止
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザーと紐付け
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //日付
    @Column(nullable = false)
    private LocalDate workDate;
    //出勤時刻
    private LocalTime startTime;
    //退勤時刻
    private LocalTime endTime;
    //休憩開始
    private LocalTime breakStart;

    //休憩終了
    private LocalTime breakEnd;

    //備考
    @Column(length = 255)
    private String note;

    // ===== getter / setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {

        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalTime getBreakStart() {
        return breakStart;
    }

    public void setBreakStart(LocalTime breakStart) {
        this.breakStart = breakStart;
    }

    public LocalTime getBreakEnd() {
        return breakEnd;
    }

    public void setBreakEnd(LocalTime breakEnd) {
        this.breakEnd = breakEnd;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
