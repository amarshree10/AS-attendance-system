package com.example.as.repository;

import com.example.as.entity.Attendance;
import com.example.as.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    //月単位取得
    List<Attendance> findByUserAndWorkDateBetween(User user, LocalDate start, LocalDate end);

    Attendance findByUserAndWorkDate(User user,LocalDate workDate);
}
