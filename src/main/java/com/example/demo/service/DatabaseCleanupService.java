package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.IpLogRepository;

@Service
public class DatabaseCleanupService {

    @Autowired
    private IpLogRepository ipLogRepository;

    @Autowired
    private LoggingService loggingService;

    // Database Cleanup Configuration
    @Value("${app.db.cleanup.keep-records:1000}")
    private int keepRecordsCount;

    public void performCleanup() {
        loggingService.logCleanupStart();
        performDatabaseCleanup();
    }

    /**
     * DATABASE CLEANUP METHOD
     * Purges all records except the last N entries as configured in
     * application.properties
     */
    private void performDatabaseCleanup() {
        try {
            long totalRecordsBefore = ipLogRepository.countTotalRecords();
            loggingService.logCleanupAnalysis(totalRecordsBefore, keepRecordsCount);

            if (totalRecordsBefore <= keepRecordsCount) {
                loggingService.logCleanupNotNeeded(totalRecordsBefore, keepRecordsCount);
                return;
            }

            int deletedRecords = ipLogRepository.deleteAllExceptLastN(keepRecordsCount);
            long totalRecordsAfter = ipLogRepository.countTotalRecords();

            loggingService.logCleanupComplete(deletedRecords, totalRecordsAfter, keepRecordsCount);

        } catch (Exception e) {
            loggingService.logCleanupError(e.getMessage());
            loggingService.logError("Database cleanup failed", e);
        }
    }
}