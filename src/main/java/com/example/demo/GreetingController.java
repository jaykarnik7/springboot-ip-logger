package com.example.demo;

import com.example.demo.services.CpuService;
import com.example.demo.services.DatabaseService;
import com.example.demo.services.DelayService;
import com.example.demo.services.MemoryService;
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

    @Autowired
    private CpuService cpuService;

    @Autowired
    private MemoryService memoryService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private DelayService delayService;

    @PostMapping("/greet")
    public String greet(
            @RequestParam String name, 
            HttpServletRequest request,
            // Load control parameters - all default to false
            @RequestParam(defaultValue = "false") boolean enableCpu,
            @RequestParam(defaultValue = "false") boolean enableMemory,
            @RequestParam(defaultValue = "false") boolean enableDbWrites,
            @RequestParam(defaultValue = "false") boolean enableDbReads,
            @RequestParam(defaultValue = "false") boolean enableDelays) {
        
        long startTime = System.currentTimeMillis();
        
        // 1. CPU OPERATIONS
        if (enableCpu) {
            cpuService.performConfigurableCpuTask();
        }
        
        // 2. MEMORY ALLOCATION
        if (enableMemory) {
            memoryService.consumeConfigurableMemory();
        }
        
        String ip = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        // Always save the main log entry
        IpLog mainLog = new IpLog();
        mainLog.setName(name);
        mainLog.setIp(ip);
        mainLog.setTimestamp(now);
        ipLogRepository.save(mainLog);

        // 3. DATABASE WRITES
        if (enableDbWrites) {
            databaseService.performConfigurableDatabaseWrites(name, ip, now);
        }
        
        // 4. DATABASE READS
        if (enableDbReads) {
            databaseService.performConfigurableDatabaseReads();
        }
        
        // 5. PROCESSING DELAYS
        if (enableDelays) {
            delayService.simulateConfigurableSlowProcessing();
        }

        // Get the last two entries for response
        List<IpLog> logs = ipLogRepository.findTop2ByOrderByTimestampDesc();

        String lastName = "N/A", lastTime = "N/A";
        if (logs.size() > 1) {
            IpLog prev = logs.get(1);
            lastName = prev.getName();
            lastTime = formatDateTime(prev.getTimestamp());
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        return String.format(
            "Hello %s!%n" +
            "Current system time: %s%n" +
            "Last query was by: %s on %s%n" +
            "Processing time: %dms%n" +
            "Load testing - CPU:%s Memory:%s DB-Writes:%s DB-Reads:%s Delays:%s%n" +
            "%s%n" +
            "Use POST /cleanup to clean database%n",
            name, 
            formatDateTime(now), 
            lastName, 
            lastTime,
            totalTime,
            enableCpu ? "✓" : "✗", 
            enableMemory ? "✓" : "✗", 
            enableDbWrites ? "✓" : "✗", 
            enableDbReads ? "✓" : "✗", 
            enableDelays ? "✓" : "✗",
            databaseService.getDatabaseStats()
        );
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
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}