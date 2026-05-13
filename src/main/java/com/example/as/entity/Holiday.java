package com.example.as.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="holiday")
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="holiday_date",unique = true,nullable = false)
    private LocalDate holidayDate;
    private String name;

    //getter/setter


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
