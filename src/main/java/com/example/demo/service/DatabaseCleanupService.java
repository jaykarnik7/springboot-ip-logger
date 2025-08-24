package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.IpLogRepository;

@Service
public class DatabaseCleanupService {

    @Autowired
    private IpLogRepository ipLogRepository;

    // Database Cleanup Configuration
    @Value("${app.db.cleanup.keep-records:1000}")
    private int keepRecordsCount;

    public void performCleanup() {
        System.out.println("Performing database cleanup...");
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
            System.out.println("Database cleanup started. Total records before cleanup: " + totalRecordsBefore);

            if (totalRecordsBefore <= keepRecordsCount) {
                System.out.println("No cleanup needed. Current records (" + totalRecordsBefore +
                        ") <= keep threshold (" + keepRecordsCount + ")");
                return;
            }

            int deletedRecords = ipLogRepository.deleteAllExceptLastN(keepRecordsCount);
            long totalRecordsAfter = ipLogRepository.countTotalRecords();

            System.out.println("Database cleanup completed successfully!");
            System.out.println("Records deleted: " + deletedRecords);
            System.out.println("Records remaining: " + totalRecordsAfter);
            System.out.println("Configured to keep last " + keepRecordsCount + " records");

        } catch (Exception e) {
            System.err.println("Database cleanup failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}