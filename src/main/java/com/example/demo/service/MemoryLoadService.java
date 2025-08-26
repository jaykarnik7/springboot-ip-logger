package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MemoryLoadService {

    @Autowired
    private LoggingService loggingService;

    private Random random = new Random();

    // Memory Configuration
    @Value("${app.load.memory.chunks:100}")
    private int memoryChunks;

    @Value("${app.load.memory.chunk-size-mb:1}")
    private int memoryChunkSizeMb;

    @Value("${app.load.memory.string-operations:100000}")
    private int stringOperations;

    @Value("${app.load.memory.hold-time-ms:500}")
    private int memoryHoldTime;

    public void performMemoryLoad() {
        int totalMB = memoryChunks * memoryChunkSizeMb;
        loggingService.logMemoryLoadStart(totalMB);
        consumeConfigurableMemory();
        loggingService.logMemoryLoadComplete(totalMB);
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
            loggingService.logStringOperationResult(result.length());

            // Hold memory for configurable time
            Thread.sleep(memoryHoldTime);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (OutOfMemoryError e) {
            loggingService.logMemoryError();
        }
    }
}