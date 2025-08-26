package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.IpLog;
import com.example.demo.IpLogRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseLoadService {

    @Autowired
    private IpLogRepository ipLogRepository;

    @Autowired
    private LoggingService loggingService;

    // Database Configuration - Common number for both reads and writes
    @Value("${app.load.db.operations:20}")
    private int dbOperations;

    // Database Configuration - number of records per query (both reads and writes)
    @Value("${app.load.db.records-per-operation:100}")
    private int dbRecordsPerOperation;

    public void performDatabaseWrites(String name, String ip, LocalDateTime now) {
        loggingService.logDbWriteStart(dbOperations, dbRecordsPerOperation);
        performConfigurableDatabaseWrites(name, ip, now);
    }

    public void performOptimizedDatabaseReads() {
        loggingService.logDbReadStart(dbOperations);
        performOptimizedDatabaseReadsInternal();
        loggingService.logDbReadComplete(dbOperations);
    }

    /**
     * OPTIMIZED BATCH DATABASE WRITES
     */
    private void performConfigurableDatabaseWrites(String name, String ip, LocalDateTime now) {
        int totalRecordsInserted = 0;
        
        // Perform the configured number of batch operations
        for (int batchNum = 0; batchNum < dbOperations; batchNum++) {
            List<IpLog> batchLogs = new ArrayList<>();
            
            // Create a batch of records
            for (int recordNum = 0; recordNum < dbRecordsPerOperation; recordNum++) {
                IpLog batchLog = new IpLog();
                batchLog.setName(name + "_batch" + batchNum + "_rec" + recordNum);
                batchLog.setIp(ip);
                batchLog.setTimestamp(now.plusSeconds(totalRecordsInserted + recordNum));
                batchLogs.add(batchLog);
            }
            
            // Save the entire batch in one operation
            ipLogRepository.saveAll(batchLogs);
            totalRecordsInserted += dbRecordsPerOperation;
            
            loggingService.logDbWriteBatch(batchNum + 1, dbRecordsPerOperation);
        }
        
        loggingService.logDbWriteComplete(dbOperations, totalRecordsInserted);
    }

    /**
     * OPTIMIZED DATABASE READS - No unnecessary delays
     */
    private void performOptimizedDatabaseReadsInternal() {
        // Perform configurable number of read operations
        for (int i = 0; i < dbOperations; i++) {
            // Read the configured number of recent records
            List<IpLog> recentLogs = ipLogRepository.findTopNByOrderByTimestampDesc(dbRecordsPerOperation);
            loggingService.logDbReadOperation(i + 1, recentLogs.size(), dbRecordsPerOperation);

            // Process some data to ensure the records are actually used
            if (!recentLogs.isEmpty()) {
                long uniqueIps = recentLogs.stream()
                        .map(IpLog::getIp)
                        .distinct()
                        .count();
                loggingService.logDbReadAnalysis(uniqueIps);
            }
        }
    }
}