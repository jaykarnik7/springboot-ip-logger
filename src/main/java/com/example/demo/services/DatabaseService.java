package com.example.demo.services;

import com.example.demo.IpLog;
import com.example.demo.IpLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class DatabaseService {

    @Autowired
    private IpLogRepository ipLogRepository;

    // Database Configuration - using consistent 'x' value
    @Value("${app.load.db.operations.count:15}")
    private int dbOperationsCount;

    @Value("${app.load.db.write.delay-ms:10}")
    private int dbWriteDelay;

    @Value("${app.load.db.read.delay-ms:50}")
    private int dbReadDelay;

    @Value("${app.load.db.connection-pool-threads:5}")
    private int connectionPoolThreads;

    // Thread pool for concurrent database operations
    private final ExecutorService dbExecutorService = Executors.newCachedThreadPool();

    /**
     * CLEAN DATABASE - Keep only last 10 entries
     */
    @Transactional
    public int cleanDatabase() {
        System.out.println("Starting database cleanup - keeping only last 10 entries...");

        // Get total count before cleanup
        long totalCount = ipLogRepository.count();

        if (totalCount <= 10) {
            System.out.println("Database has " + totalCount + " entries, no cleanup needed.");
            return 0;
        }

        // Get the last 10 entries by timestamp
        List<IpLog> lastTenEntries = ipLogRepository.findTop10ByOrderByTimestampDesc();

        if (lastTenEntries.isEmpty()) {
            System.out.println("No entries found to preserve.");
            return 0;
        }

        // Get the timestamp of the 10th entry (oldest of the ones we want to keep)
        LocalDateTime cutoffTimestamp = lastTenEntries.get(lastTenEntries.size() - 1).getTimestamp();

        // Delete all entries older than the cutoff
        int deletedCount = ipLogRepository.deleteByTimestampBefore(cutoffTimestamp);

        System.out.println("Database cleanup completed: " + deletedCount + " entries deleted, " +
                (totalCount - deletedCount) + " entries remaining");

        return deletedCount;
    }

    /**
     * CONFIGURABLE DATABASE WRITES with multiple connections
     */
    public void performConfigurableDatabaseWrites(String name, String ip, LocalDateTime now) {
        System.out.println("Performing " + dbOperationsCount + " database writes using " +
                connectionPoolThreads + " concurrent connections...");

        // Create futures for concurrent database writes
        List<CompletableFuture<Void>> writeFutures = IntStream.range(0, dbOperationsCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        IpLog additionalLog = new IpLog();
                        additionalLog.setName(name + "_write_" + (i + 1));
                        additionalLog.setIp(ip);
                        additionalLog.setTimestamp(now.plusNanos(i * 1000000L)); // Add nanoseconds for unique
                                                                                 // timestamps

                        ipLogRepository.save(additionalLog);

                        System.out.println("DB Write " + (i + 1) + " completed by thread: " +
                                Thread.currentThread().getName());

                        // Add configurable delay between saves
                        if (dbWriteDelay > 0) {
                            Thread.sleep(dbWriteDelay);
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("DB Write " + (i + 1) + " interrupted");
                    } catch (Exception e) {
                        System.err.println("DB Write " + (i + 1) + " failed: " + e.getMessage());
                    }
                }, dbExecutorService))
                .toList();

        // Wait for all writes to complete
        CompletableFuture.allOf(writeFutures.toArray(new CompletableFuture[0])).join();

        System.out.println("Completed " + dbOperationsCount + " concurrent database write operations");
    }

    /**
     * CONFIGURABLE DATABASE READS with multiple connections (last X entries only)
     */
    @SuppressWarnings("unused")
    public void performConfigurableDatabaseReads() {
        System.out.println("Performing " + dbOperationsCount + " database reads using " +
                connectionPoolThreads + " concurrent connections...");

        // Create futures for concurrent database reads
        List<CompletableFuture<Void>> readFutures = IntStream.range(0, dbOperationsCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        // Read last X entries (using the same count as writes for consistency)
                        List<IpLog> logs = ipLogRepository.findTopByOrderByTimestampDesc(dbOperationsCount);

                        System.out.println("DB Read " + (i + 1) + " completed by thread: " +
                                Thread.currentThread().getName() +
                                " - Found " + logs.size() + " recent records");

                        // Add some processing to make the read operation more realistic
                        for (IpLog log : logs) {
                            // Simple processing to ensure the data is actually used
                            String processed = log.getName() + "@" + log.getIp() + ":" + log.getTimestamp();
                        }

                        // Configurable delay between reads
                        if (dbReadDelay > 0) {
                            Thread.sleep(dbReadDelay);
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("DB Read " + (i + 1) + " interrupted");
                    } catch (Exception e) {
                        System.err.println("DB Read " + (i + 1) + " failed: " + e.getMessage());
                    }
                }, dbExecutorService))
                .toList();

        // Wait for all reads to complete
        CompletableFuture.allOf(readFutures.toArray(new CompletableFuture[0])).join();

        System.out.println("Completed " + dbOperationsCount + " concurrent database read operations");
    }

    /**
     * Get database statistics
     */
    public String getDatabaseStats() {
        try {
            long totalCount = ipLogRepository.count();
            List<IpLog> recent = ipLogRepository.findTop2ByOrderByTimestampDesc();

            return String.format("Total DB entries: %d, Recent entries: %d",
                    totalCount, recent.size());
        } catch (Exception e) {
            return "Unable to fetch database statistics: " + e.getMessage();
        }
    }
}