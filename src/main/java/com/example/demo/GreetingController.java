package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@RestController
public class GreetingController {

    @Autowired
    private IpLogRepository ipLogRepository;

    private Random random = new Random();

    // CONFIGURABLE PARAMETERS FROM APPLICATION.PROPERTIES
    
    // CPU Configuration
    @Value("${app.load.cpu.fibonacci.count:5}")
    private int fibonacciCount;
    
    @Value("${app.load.cpu.fibonacci.base:35}")
    private int fibonacciBase;
    
    @Value("${app.load.cpu.sorting.rounds:3}")
    private int sortingRounds;
    
    @Value("${app.load.cpu.sorting.array-size:50000}")
    private int sortingArraySize;
    
    // Memory Configuration
    @Value("${app.load.memory.chunks:100}")
    private int memoryChunks;
    
    @Value("${app.load.memory.chunk-size-mb:1}")
    private int memoryChunkSizeMb;
    
    @Value("${app.load.memory.string-operations:100000}")
    private int stringOperations;
    
    @Value("${app.load.memory.hold-time-ms:500}")
    private int memoryHoldTime;
    
    // Database Configuration
    @Value("${app.load.db.write.extra-entries:10}")
    private int extraDbWrites;
    
    @Value("${app.load.db.write.delay-ms:10}")
    private int dbWriteDelay;
    
    @Value("${app.load.db.read.operations:20}")
    private int dbReadOperations;
    
    @Value("${app.load.db.read.full-scan-interval:5}")
    private int dbFullScanInterval;
    
    @Value("${app.load.db.read.delay-ms:50}")
    private int dbReadDelay;
    
    // Processing Delay Configuration
    @Value("${app.load.delay.external-calls:3}")
    private int externalServiceCalls;
    
    @Value("${app.load.delay.external-call-ms:200}")
    private int externalCallDelay;
    
    @Value("${app.load.delay.math-operations:1000000}")
    private int mathOperations;
    
    // Feature Toggle Configuration
    @Value("${app.load.enable.cpu:true}")
    private boolean enableCpuLoad;
    
    @Value("${app.load.enable.memory:true}")
    private boolean enableMemoryLoad;
    
    @Value("${app.load.enable.db-writes:true}")
    private boolean enableExtraDbWrites;
    
    @Value("${app.load.enable.db-reads:true}")
    private boolean enableExtraDbReads;
    
    @Value("${app.load.enable.delays:true}")
    private boolean enableProcessingDelays;

    @PostMapping("/greet")
    public String greet(@RequestParam String name, HttpServletRequest request) {
        System.out.println("Starting configurable heavy processing for: " + name);
        System.out.println("Configuration - CPU:" + enableCpuLoad + " Memory:" + enableMemoryLoad + 
                          " DB-Writes:" + enableExtraDbWrites + " DB-Reads:" + enableExtraDbReads + 
                          " Delays:" + enableProcessingDelays);
        
        // 1. CONFIGURABLE CPU OPERATIONS
        if (enableCpuLoad) {
            System.out.println("Performing CPU-intensive operations (Fibonacci:" + fibonacciCount + 
                             ", Sorting rounds:" + sortingRounds + ")...");
            performConfigurableCpuTask();
        }
        
        // 2. CONFIGURABLE MEMORY ALLOCATION
        if (enableMemoryLoad) {
            System.out.println("Allocating memory (" + (memoryChunks * memoryChunkSizeMb) + "MB)...");
            consumeConfigurableMemory();
        }
        
        String ip = request.getRemoteAddr();
        LocalDateTime now = LocalDateTime.now();

        // Always save the main log entry
        IpLog mainLog = new IpLog();
        mainLog.setName(name);
        mainLog.setIp(ip);
        mainLog.setTimestamp(now);
        ipLogRepository.save(mainLog);

        // 3. CONFIGURABLE ADDITIONAL DATABASE WRITES
        if (enableExtraDbWrites) {
            System.out.println("Performing " + extraDbWrites + " additional database writes...");
            performConfigurableDatabaseWrites(name, ip, now);
        }
        
        // 4. CONFIGURABLE DATABASE READS
        if (enableExtraDbReads) {
            System.out.println("Performing " + dbReadOperations + " database read operations...");
            performConfigurableDatabaseReads();
        }
        
        // 5. CONFIGURABLE PROCESSING DELAYS
        if (enableProcessingDelays) {
            System.out.println("Simulating " + externalServiceCalls + " external calls with " + 
                             externalCallDelay + "ms delay each...");
            simulateConfigurableSlowProcessing();
        }

        // Get the last two entries for response
        List<IpLog> logs = ipLogRepository.findTop2ByOrderByTimestampDesc();

        String lastName = "N/A", lastTime = "N/A";
        if (logs.size() > 1) {
            IpLog prev = logs.get(1);
            lastName = prev.getName();
            lastTime = formatDateTime(prev.getTimestamp());
        }

        System.out.println("Completed configurable heavy processing for: " + name);
        
        return String.format(
            "Hello %s!%nThe current system time is %s%nThe last query was by - %s on %s%n" +
            "Configurable processing completed - CPU:%s Memory:%s DB-Writes:%s DB-Reads:%s Delays:%s%n",
            name, formatDateTime(now), lastName, lastTime,
            enableCpuLoad ? "✓" : "✗", enableMemoryLoad ? "✓" : "✗", 
            enableExtraDbWrites ? "✓" : "✗", enableExtraDbReads ? "✓" : "✗", 
            enableProcessingDelays ? "✓" : "✗"
        );
    }

    /**
     * CONFIGURABLE CPU-INTENSIVE TASK
     */
    private void performConfigurableCpuTask() {
        // Calculate configurable number of Fibonacci numbers
        for (int i = 0; i < fibonacciCount; i++) {
            int fibNumber = fibonacciBase + i;
            long result = calculateFibonacci(fibNumber);
            System.out.println("Fibonacci(" + fibNumber + ") = " + result);
        }
        
        // Additional CPU work: Sort large arrays
        for (int i = 0; i < sortingRounds; i++) {
            performConfigurableSortingWork();
        }
    }

    /**
     * Configurable sorting work
     */
    private void performConfigurableSortingWork() {
        List<Integer> numbers = new ArrayList<>();
        // Create a configurable-sized list of random numbers
        for (int i = 0; i < sortingArraySize; i++) {
            numbers.add(random.nextInt(1000000));
        }
        // Sort it (bubble sort for more CPU usage)
        bubbleSort(new ArrayList<>(numbers));
    }

    /**
     * CONFIGURABLE MEMORY-INTENSIVE TASK
     */
    private void consumeConfigurableMemory() {
        List<byte[]> memoryHogs = new ArrayList<>();
        
        try {
            // Allocate configurable amount of memory
            for (int i = 0; i < memoryChunks; i++) {
                // Allocate configurable-sized chunks
                byte[] chunk = new byte[memoryChunkSizeMb * 1024 * 1024];
                // Fill with random data to ensure it's actually allocated
                random.nextBytes(chunk);
                memoryHogs.add(chunk);
            }
            
            // Create configurable string operations
            StringBuilder largeString = new StringBuilder();
            for (int i = 0; i < stringOperations; i++) {
                largeString.append("This is memory consuming text for testing purposes. ");
            }
            
            // Process the string to ensure JVM doesn't optimize it away
            String result = largeString.toString();
            System.out.println("Created large string of length: " + result.length());
            
            // Hold memory for configurable time
            Thread.sleep(memoryHoldTime);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (OutOfMemoryError e) {
            System.err.println("Out of memory! Continuing...");
        }
        
        System.out.println("Memory allocation completed: " + 
                          (memoryChunks * memoryChunkSizeMb) + "MB allocated");
    }

    /**
     * CONFIGURABLE DATABASE WRITES
     */
    private void performConfigurableDatabaseWrites(String name, String ip, LocalDateTime now) {
        // Create configurable number of additional log entries
        for (int i = 1; i <= extraDbWrites; i++) {
            IpLog additionalLog = new IpLog();
            additionalLog.setName(name + "_extra_" + i);
            additionalLog.setIp(ip);
            additionalLog.setTimestamp(now.plusSeconds(i));
            ipLogRepository.save(additionalLog);
            
            // Add configurable delay between saves
            if (dbWriteDelay > 0) {
                try {
                    Thread.sleep(dbWriteDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Completed " + (extraDbWrites + 1) + " database write operations");
    }

    /**
     * CONFIGURABLE DATABASE READS
     */
    private void performConfigurableDatabaseReads() {
        // Perform configurable number of read operations
        for (int i = 0; i < dbReadOperations; i++) {
            List<IpLog> logs = ipLogRepository.findTop2ByOrderByTimestampDesc();
            System.out.println("Read operation " + (i + 1) + ": Found " + logs.size() + " records");
            
            // Additional read operation - get all records (expensive!) at configurable interval
            if (dbFullScanInterval > 0 && i % dbFullScanInterval == 0) {
                List<IpLog> allLogs = ipLogRepository.findAll();
                System.out.println("Full table scan " + (i/dbFullScanInterval + 1) + 
                                 ": Found " + allLogs.size() + " total records");
            }
            
            // Configurable delay between reads
            if (dbReadDelay > 0) {
                try {
                    Thread.sleep(dbReadDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Completed " + dbReadOperations + " database read operations");
    }

    /**
     * CONFIGURABLE SLOW PROCESSING
     */
    private void simulateConfigurableSlowProcessing() {
        try {
            // Simulate configurable number of external service calls
            for (int i = 0; i < externalServiceCalls; i++) {
                System.out.println("Simulating external service call " + (i + 1) + "...");
                Thread.sleep(externalCallDelay);
            }
            
            // Simulate configurable complex calculations
            for (int i = 0; i < mathOperations; i++) {
                Math.sin(i) * Math.cos(i) * Math.tan(i);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Slow processing simulation completed: " + 
                          externalServiceCalls + " external calls, " + 
                          mathOperations + " math operations");
    }

    /**
     * Recursive Fibonacci calculation (intentionally inefficient for CPU load)
     */
    private long calculateFibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return calculateFibonacci(n - 1) + calculateFibonacci(n - 2);
    }

    /**
     * Inefficient bubble sort for CPU consumption
     */
    private void bubbleSort(List<Integer> arr) {
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    // Swap
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * FORMAT DATE TIME (unchanged from original)
     */
    private String formatDateTime(LocalDateTime dt) {
        String time = dt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String dow = dt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int day = dt.getDayOfMonth(), year = dt.getYear();
        String month = dt.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return time + " on the " + day + getOrdinal(day) + " of " + month + ", " + year + " (" + dow + ")";
    }

    /**
     * GET ORDINAL (unchanged from original)
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