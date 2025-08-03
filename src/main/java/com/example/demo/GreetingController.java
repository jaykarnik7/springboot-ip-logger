package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@RestController
public class GreetingController {

    @Autowired
    private IpLogRepository ipLogRepository;

    @PostMapping("/greet")
    public String greet(@RequestParam String name, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        IpLog currentLog = new IpLog();
        currentLog.setName(name); currentLog.setIp(ip); currentLog.setTimestamp(now);
        ipLogRepository.save(currentLog);

        List<IpLog> logs = ipLogRepository.findTop2ByOrderByTimestampDesc();

        String lastName = "N/A", lastTime = "N/A";
        if (logs.size() > 1) {
            IpLog prev = logs.get(1);
            lastName = prev.getName();
            lastTime = formatDateTime(prev.getTimestamp());
        }

        return String.format(
            "Hello %s!%nThe current system time is %s%nThe last query was by - %s on %s%n",
            name, formatDateTime(now), lastName, lastTime
        );
    }

    private String formatDateTime(LocalDateTime dt) {
        String time = dt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int day = dt.getDayOfMonth(), year = dt.getYear();
        String month = dt.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return time + " on the " + day + getOrdinal(day) + " of " + month + ", " + year + " (" + dow + ")";
    }

    private String getOrdinal(int day) {
        if (day >= 11 && day <= 13) return "th";
        return switch (day) {
            case 1, 21, 31 -> "st";
            case 2, 22     -> "nd";
            case 3, 23     -> "rd";
            default        -> "th";
        };
    }
}
