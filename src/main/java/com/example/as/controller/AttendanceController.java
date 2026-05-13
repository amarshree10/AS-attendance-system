package com.example.as.controller;

import com.example.as.dto.AttendanceRow;
import com.example.as.entity.Attendance;
import com.example.as.entity.Holiday;
import com.example.as.entity.User;
import com.example.as.repository.AttendanceRepository;
import com.example.as.repository.HolidayRepository;
import com.example.as.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class AttendanceController {

    @Autowired
    private AttendanceRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HolidayRepository holidayRepo;

    // =========================
    // 一覧表示（1ヶ月）
    // =========================
    @GetMapping("/attendance")
    public String view(Model model,
                       @AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(required = false) Integer year,
                       @RequestParam(required = false) Integer month) {

        // ログインユーザー取得
        User user = userRepo.findByUsername(userDetails.getUsername());

        // 月決定
        YearMonth ym;
        if (year != null && month != null) {
            ym = YearMonth.of(year, month);
        } else {
            ym = YearMonth.now();
        }

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // DB取得
        List<Attendance> list = repo.findByUserAndWorkDateBetween(user, start, end);

        // Map化（高速参照）
        Map<LocalDate, Attendance> map =
                list.stream().collect(Collectors.toMap(Attendance::getWorkDate, a -> a));

        //祝日取得
        List<Holiday> holiday = holidayRepo.findByHolidayDateBetween(start, end);

        //高速判定用Setに変換
        Set<LocalDate> holidaySet = holiday.stream()
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toSet());

        // 画面用DTO生成
        List<AttendanceRow> rows = new ArrayList<>();
        long totalMonthMinutes = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {

            AttendanceRow row = new AttendanceRow();
            row.setWorkDate(d);

            Attendance a = map.get(d);

            if (a != null) {
                row.setStartTime(a.getStartTime() != null ? a.getStartTime().toString() : "");
                row.setEndTime(a.getEndTime() != null ? a.getEndTime().toString() : "");
                //休憩時間
                row.setBreakStart(a.getBreakStart() != null ? a.getBreakStart().toString() : "");
                row.setBreakEnd(a.getBreakEnd() != null ? a.getBreakEnd().toString() : "");
                row.setNote(a.getNote());

                // 勤務時間計算
                if (a.getStartTime() != null && a.getEndTime() != null) {
                    long breakMinutes = 0;
                    //休憩時間
                    if (a.getBreakStart() != null && a.getBreakEnd() != null) {
                        breakMinutes = Duration.between(a.getBreakStart(), a.getBreakEnd()).toMinutes();
                    }
                    //総勤務時間
                    long workMinutes = Duration.between(a.getStartTime(), a.getEndTime()).toMinutes();

                    //実勤務
                    long actualMinutes = workMinutes - breakMinutes;
                    totalMonthMinutes += actualMinutes;
                    long h = actualMinutes / 60;
                    long m = actualMinutes % 60;
                    row.setWorkTime(String.format("%02d:%02d", h, m));
                }
            }
            rows.add(row);
        }
        //月合計
        long totalHours = totalMonthMinutes / 60;
        long remainMinutes = totalMonthMinutes % 60;
        String totalWorkTime = String.format("%02d:%02d", totalHours, remainMinutes);

        model.addAttribute("rows", rows);

        model.addAttribute("year", ym.getYear());
        model.addAttribute("month", ym.getMonthValue());

        model.addAttribute("username", user.getUsername());
        model.addAttribute("firstName", user.getFirstname());
        model.addAttribute("lastName", user.getLastname());

        model.addAttribute("holidaySet", holidaySet);

        //月合計勤務時間
        model.addAttribute("totalWorkTime", totalWorkTime);
        return "attendance";
    }

    // =========================
    // 一括保存
    // =========================
    @PostMapping("/attendance/save")
    @Transactional
    public String save(@RequestParam Map<String, String> params,
                       @RequestParam Integer year,
                       @RequestParam Integer month,
                       @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {

        User user = userRepo.findByUsername(userDetails.getUsername());

        int i = 0;

        while (params.containsKey("rows[" + i + "].workDate")) {

            LocalDate date = LocalDate.parse(params.get("rows[" + i + "].workDate"));
            String start = params.get("rows[" + i + "].startTime");
            String end = params.get("rows[" + i + "].endTime");
            String break_Start = params.get("rows[" + i + "].breakStart");
            String break_End = params.get("rows[" + i + "].breakEnd");

            String note = params.get("rows[" + i + "].note");

            Attendance att = repo.findByUserAndWorkDate(user, date);

            if (att == null) {
                att = new Attendance();
                att.setUser(user);
                att.setWorkDate(date);
            }
            if (start != null && end != null &&
                    !start.isEmpty() && !end.isEmpty()) {
                LocalTime st = LocalTime.parse(start);
                LocalTime et = LocalTime.parse(end);

                if (et.isBefore(st)) {
                    redirectAttributes.addFlashAttribute(
                            "errorMessage",
                            date + ":End time must be after start time.");
                    return "redirect:/attendance?year=" + year + "&month=" + month;
                }
                //勤務時間計算
                long workMinutes = Duration.between(st, et).toMinutes();
                //休憩時間
                if (break_Start != null && break_End != null &&
                        !break_Start.isEmpty() && !break_End.isEmpty()) {
                    LocalTime bs = LocalTime.parse(break_Start);
                    LocalTime be = LocalTime.parse(break_End);

                    if (be.isBefore(bs)) {
                        redirectAttributes.addFlashAttribute(
                                "errorMessage",
                                date + ":Break end time must be after break start time.");
                        return "redirect:/attendance?year=" + year + "&month=" + month;
                    }
                    //休憩時間計算
                    long breakMinutes = Duration.between(bs, be).toMinutes();
                    //休憩時間が勤務時間外
                    if (bs.isBefore(st) || be.isAfter(et)) {
                        redirectAttributes.addFlashAttribute(
                                "errorMessage",
                                date + ":Break start must be within work hours.");
                        return "redirect:/attendance?year=" + year + "&month=" + month;
                    }

                    //勤務時間は８時間以上の場合は１時間休憩制
                    if (workMinutes >= 480 && breakMinutes < 60) {
                        redirectAttributes.addFlashAttribute(
                                "errorMessage",
                                date + ":A 1-hour break is required for work periods exceeding 8 hours.");
                        return "redirect:/attendance?year=" + year + "&month=" + month;
                    }
                    //勤務時間は6時間以上の場合は45分間休憩制
                    if (workMinutes >= 360 && breakMinutes < 45) {
                        redirectAttributes.addFlashAttribute(
                                "errorMessage",
                                date + ":A 45-minutes break is required for work periods exceeding 6 hours.");
                        return "redirect:/attendance?year=" + year + "&month=" + month;
                    }
                    att.setBreakStart(bs);
                    att.setBreakEnd(be);
                }
                att.setStartTime(st);
                att.setEndTime(et);
            } else {
                att.setStartTime(null);
                att.setEndTime(null);
                att.setBreakStart(null);
                att.setBreakEnd(null);
            }

            att.setNote(note);
            repo.save(att);
            i++;
        }
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Attendance saved successfully");
        return "redirect:/attendance?year=" + year + "&month=" + month;

    }
}