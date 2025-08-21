package com.example.demo.services;

import com.example.demo.IpLog;
import com.example.demo.IpLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * CLEAN DATABASE - Keep only last 10 entries
     */
    @Transactional
    public int cleanDatabase() {
        // Get total count before cleanup
        long totalCount = ipLogRepository.count();
        
        if (totalCount <= 10) {
            return 0;
        }
        
        // Get the last 10 entries by timestamp
        List<IpLog> lastTenEntries = ipLogRepository.findTop10ByOrderByTimestampDesc();
        
        if (lastTenEntries.isEmpty()) {
            return 0;
        }
        
        // Get the timestamp of the 10th entry (oldest of the ones we want to keep)
        LocalDateTime cutoffTimestamp = lastTenEntries.get(lastTenEntries.size() - 1).getTimestamp();
        
        // Delete all entries older than the cutoff
        int deletedCount = ipLogRepository.deleteByTimestampBefore(cutoffTimestamp);
        
        return deletedCount;
    }

    /**
     * SIMPLE DATABASE WRITES (no threading overhead)
     */
    public void performConfigurableDatabaseWrites(String name, String ip, LocalDateTime now) {
        // Simple sequential writes - much faster for small operations
        for (int i = 0; i < dbOperationsCount; i++) {
            try {
                IpLog additionalLog = new IpLog();
                additionalLog.setName(name + "_write_" + (i + 1));
                additionalLog.setIp(ip);
                additionalLog.setTimestamp(now.plusNanos(i * 1000000L));
                
                ipLogRepository.save(additionalLog);
                
                // Add configurable delay between saves
                if (dbWriteDelay > 0) {
                    Thread.sleep(dbWriteDelay);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Continue with other operations
            }
        }
    }

    /**
     * SIMPLE DATABASE READS (no threading overhead)
     */
    @SuppressWarnings("unused")
    public void performConfigurableDatabaseReads() {
        // Simple sequential reads
        for (int i = 0; i < dbOperationsCount; i++) {
            try {
                // Read last X entries (using the same count as writes for consistency)
                List<IpLog> logs = ipLogRepository.findTopByOrderByTimestampDesc(dbOperationsCount);
                
                // Simple processing to ensure the data is actually used
                for (IpLog log : logs) {
                    String processed = log.getName() + "@" + log.getIp();
                }
                
                // Configurable delay between reads
                if (dbReadDelay > 0) {
                    Thread.sleep(dbReadDelay);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // Continue with other operations
            }
        }
    }

    /**
     * Get database statistics
     */
    public String getDatabaseStats() {
        try {
            long totalCount = ipLogRepository.count();
            return "Total DB entries: " + totalCount;
        } catch (Exception e) {
            return "Stats unavailable";
        }
    }
}