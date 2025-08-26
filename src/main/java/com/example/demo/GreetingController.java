package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CpuLoadService;
import com.example.demo.service.DatabaseCleanupService;
import com.example.demo.service.DatabaseLoadService;
import com.example.demo.service.DelayLoadService;
import com.example.demo.service.LoggingService;
import com.example.demo.service.MemoryLoadService;

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
    
    @Autowired
    private CpuLoadService cpuLoadService;
    
    @Autowired
    private MemoryLoadService memoryLoadService;
    
    @Autowired
    private DatabaseLoadService databaseLoadService;
    
    @Autowired
    private DelayLoadService delayLoadService;
    
    @Autowired
    private DatabaseCleanupService databaseCleanupService;

    @Autowired
    private LoggingService loggingService;

    @PostMapping("/greet")
    public String greet(
            @RequestParam String name,
            HttpServletRequest request,
            // Load control parameters - all default to false
            @RequestParam(defaultValue = "false") boolean enableCpu,
            @RequestParam(defaultValue = "false") boolean enableMemory,
            @RequestParam(defaultValue = "false") boolean enableDbWrites,
            @RequestParam(defaultValue = "false") boolean enableDbReads,
            @RequestParam(defaultValue = "false") boolean enableDelays,
            // Database cleanup parameter
            @RequestParam(defaultValue = "false") boolean enableCleanup) {

        String ip = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        // Log request start with configuration
        loggingService.logRequestStart(name, ip, enableCpu, enableMemory, 
                                     enableDbWrites, enableDbReads, enableDelays, enableCleanup);

        // OPTIONAL DATABASE CLEANUP
        if (enableCleanup) {
            databaseCleanupService.performCleanup();
        }

        // 1. CONFIGURABLE CPU OPERATIONS
        if (enableCpu) {
            cpuLoadService.performCpuLoad();
        }

        // 2. CONFIGURABLE MEMORY ALLOCATION
        if (enableMemory) {
            memoryLoadService.performMemoryLoad();
        }

        // Always save the main log entry
        IpLog mainLog = new IpLog();
        mainLog.setName(name);
        mainLog.setIp(ip);
        mainLog.setTimestamp(now);
        ipLogRepository.save(mainLog);

        // 3. CONFIGURABLE ADDITIONAL DATABASE WRITES
        if (enableDbWrites) {
            databaseLoadService.performDatabaseWrites(name, ip, now);
        }

        // OPTIMIZED DATABASE READS
        if (enableDbReads) {
            databaseLoadService.performOptimizedDatabaseReads();
        }

        // CONFIGURABLE PROCESSING DELAYS
        if (enableDelays) {
            delayLoadService.performDelayLoad();
        }

        // Get the last two entries for response
        List<IpLog> logs = ipLogRepository.findTop2ByOrderByTimestampDesc();

        String lastName = "N/A", lastTime = "N/A";
        if (logs.size() > 1) {
            IpLog prev = logs.get(1);
            lastName = prev.getName();
            lastTime = formatDateTime(prev.getTimestamp());
        }

        // Log request completion
        loggingService.logRequestComplete(name);

        return String.format(
                "Hello %s!%nThe current system time is %s%nThe last query was by - %s on %s%n" +
                        "Load testing executed - CPU:%s Memory:%s DB-Writes:%s DB-Reads:%s Delays:%s Cleanup:%s%n",
                name, formatDateTime(now), lastName, lastTime,
                enableCpu ? "✓" : "✗", enableMemory ? "✓" : "✗",
                enableDbWrites ? "✓" : "✗", enableDbReads ? "✓" : "✗",
                enableDelays ? "✓" : "✗", enableCleanup ? "✓" : "✗");
    }

    /**
     * FORMAT DATE TIME
     */
    private String formatDateTime(LocalDateTime dt) {
        String time = dt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int day = dt.getDayOfMonth();
        int year = dt.getYear();
        String month = dt.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return time + " on the " + day + getOrdinal(day) + " of " + month + ", " + year + " (" + dow + ")";
    }

    /**
     * GET ORDINAL
     */
    private String getOrdinal(int day) {
        if (day >= 11 && day <= 13)
            return "th";
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}