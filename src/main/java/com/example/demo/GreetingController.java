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
        
        System.out.println("=".repeat(80));
        System.out.println("Starting configurable load testing for: " + name);
        System.out.println("Load Configuration - CPU:" + enableCpu + " Memory:" + enableMemory + 
                          " DB-Writes:" + enableDbWrites + " DB-Reads:" + enableDbReads + 
                          " Delays:" + enableDelays);
        System.out.println("=".repeat(80));
        
        long startTime = System.currentTimeMillis();
        
        // 1. CONFIGURABLE CPU OPERATIONS
        if (enableCpu) {
            System.out.println("\n[1/5] CPU OPERATIONS");
            System.out.println("-".repeat(40));
            cpuService.performConfigurableCpuTask();
        }
        
        // 2. CONFIGURABLE MEMORY ALLOCATION
        if (enableMemory) {
            System.out.println("\n[2/5] MEMORY OPERATIONS");
            System.out.println("-".repeat(40));
            memoryService.consumeConfigurableMemory();
        }
        
        String ip = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        // Always save the main log entry
        System.out.println("\n[CORE] SAVING MAIN LOG ENTRY");
        System.out.println("-".repeat(40));
        IpLog mainLog = new IpLog();
        mainLog.setName(name);
        mainLog.setIp(ip);
        mainLog.setTimestamp(now);
        ipLogRepository.save(mainLog);
        System.out.println("Main log entry saved for: " + name + " from IP: " + ip);

        // 3. CONFIGURABLE DATABASE WRITES (with multiple connections)
        if (enableDbWrites) {
            System.out.println("\n[3/5] DATABASE WRITE OPERATIONS");
            System.out.println("-".repeat(40));
            databaseService.performConfigurableDatabaseWrites(name, ip, now);
        }
        
        // 4. CONFIGURABLE DATABASE READS (with multiple connections)
        if (enableDbReads) {
            System.out.println("\n[4/5] DATABASE READ OPERATIONS");
            System.out.println("-".repeat(40));
            databaseService.performConfigurableDatabaseReads();
        }
        
        // 5. CONFIGURABLE PROCESSING DELAYS
        if (enableDelays) {
            System.out.println("\n[5/5] PROCESSING DELAY OPERATIONS");
            System.out.println("-".repeat(40));
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

        System.out.println("\n" + "=".repeat(80));
        System.out.println("LOAD TESTING COMPLETED for: " + name);
        System.out.println("Total execution time: " + totalTime + "ms");
        System.out.println("Database stats: " + databaseService.getDatabaseStats());
        if (enableMemory) {
            System.out.println("Memory stats: " + memoryService.getMemoryStats());
        }
        System.out.println("=".repeat(80));
        
        return String.format(
            "Hello %s!%n" +
            "🕒 Current system time: %s%n" +
            "📝 Last query was by: %s on %s%n" +
            "⚡ Total processing time: %dms%n" +
            "%n🧪 Load testing executed:%n" +
            "  CPU: %s | Memory: %s | DB-Writes: %s | DB-Reads: %s | Delays: %s%n" +
            "%n📊 System Info:%n" +
            "  %s%n" +
            "%s%n" +
            "%n💡 Use POST /cleanup to clean database (keeps last 10 entries)%n",
            name, 
            formatDateTime(now), 
            lastName, 
            lastTime,
            totalTime,
            enableCpu ? "✅" : "❌", 
            enableMemory ? "✅" : "❌", 
            enableDbWrites ? "✅" : "❌", 
            enableDbReads ? "✅" : "❌", 
            enableDelays ? "✅" : "❌",
            databaseService.getDatabaseStats(),
            enableMemory ? "  " + memoryService.getMemoryStats() : ""
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