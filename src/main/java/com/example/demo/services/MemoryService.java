package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MemoryService {

    private final Random random = new Random();

    // Memory Configuration
    @Value("${app.load.memory.chunks:100}")
    private int memoryChunks;
    
    @Value("${app.load.memory.chunk-size-mb:1}")
    private int memoryChunkSizeMb;
    
    @Value("${app.load.memory.string-operations:100000}")
    private int stringOperations;
    
    @Value("${app.load.memory.hold-time-ms:500}")
    private int memoryHoldTime;

    /**
     * CONFIGURABLE MEMORY-INTENSIVE TASK
     */
    public void consumeConfigurableMemory() {
        System.out.println("Starting memory allocation (" + (memoryChunks * memoryChunkSizeMb) + "MB)...");
        
        List<byte[]> memoryHogs = new ArrayList<>();
        
        try {
            // Allocate configurable amount of memory
            for (int i = 0; i < memoryChunks; i++) {
                // Allocate configurable-sized chunks
                byte[] chunk = new byte[memoryChunkSizeMb * 1024 * 1024];
                // Fill with random data to ensure it's actually allocated
                random.nextBytes(chunk);
                memoryHogs.add(chunk);
                
                if ((i + 1) % 10 == 0) {
                    System.out.println("Allocated " + (i + 1) + "/" + memoryChunks + " memory chunks");
                }
            }
            
            // Create configurable string operations
            StringBuilder largeString = new StringBuilder();
            System.out.println("Starting " + stringOperations + " string operations...");
            
            for (int i = 0; i < stringOperations; i++) {
                largeString.append("This is memory consuming text for testing purposes. ");
                
                if ((i + 1) % (stringOperations / 10) == 0) {
                    System.out.println("Completed " + (i + 1) + "/" + stringOperations + " string operations");
                }
            }
            
            // Process the string to ensure JVM doesn't optimize it away
            String result = largeString.toString();
            System.out.println("Created large string of length: " + result.length());
            
            // Hold memory for configurable time
            System.out.println("Holding memory allocation for " + memoryHoldTime + "ms...");
            Thread.sleep(memoryHoldTime);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Memory allocation interrupted");
        } catch (OutOfMemoryError e) {
            System.err.println("Out of memory! Continuing... Available memory chunks: " + memoryHogs.size());
        }
        
        System.out.println("Memory allocation completed: " + 
                          (memoryChunks * memoryChunkSizeMb) + "MB allocated and released");
    }

    /**
     * Get current memory usage statistics
     */
    public String getMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        return String.format("Memory Usage - Used: %.2fMB, Free: %.2fMB, Total: %.2fMB, Max: %.2fMB",
                           usedMemory / (1024.0 * 1024.0),
                           freeMemory / (1024.0 * 1024.0),
                           totalMemory / (1024.0 * 1024.0),
                           maxMemory / (1024.0 * 1024.0));
    }
}