package com.example.as.service;

import com.example.as.entity.Holiday;
import com.example.as.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;

@Service
public class HolidayService {
    @Autowired
    private HolidayRepository holidayRepo;

    public void synHolidays() throws Exception {
        URL url = new URL("https://holidays-jp.github.io/api/v1/date.json");
        ObjectMapper mapper = new ObjectMapper();
        //key:yyyy-MM-dd  value:holiday
        Map<String, String> data = mapper.readValue(url, Map.class);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());
            //DBに存在しない場合のみ登録（重複防止）
            if (!holidayRepo.existsByHolidayDate(date)) {
                Holiday h = new Holiday();
                h.setHolidayDate(date);
                h.setName(entry.getValue());
                holidayRepo.save(h);
            }
        }
    }
}
