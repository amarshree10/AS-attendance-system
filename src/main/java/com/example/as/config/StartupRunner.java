package com.example.as.config;

import com.example.as.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    @Autowired
    private HolidayService holidayService;
    @Override
    public void run(String...args)throws Exception
    {
        //起動時に祝日DBへ同期
        holidayService.synHolidays();
    }
}
