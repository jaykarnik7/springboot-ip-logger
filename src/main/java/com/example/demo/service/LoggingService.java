package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Centralized logging service for the application.
 * Provides structured logging methods with consistent formatting.
 */
@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    // Controller Logging
    public void logRequestStart(String name, String ip, boolean enableCpu, boolean enableMemory, 
                               boolean enableDbWrites, boolean enableDbReads, boolean enableDelays, 
                               boolean enableCleanup) {
        logger.info("Processing request for user '{}' from IP '{}' - Load config: CPU:{}, Memory:{}, DB-Writes:{}, DB-Reads:{}, Delays:{}, Cleanup:{}", 
                   name, ip, enableCpu, enableMemory, enableDbWrites, enableDbReads, enableDelays, enableCleanup);
    }

    public void logRequestComplete(String name) {
        logger.info("Request processing completed for user '{}'", name);
    }

    // CPU Load Logging
    public void logCpuLoadStart(int fibonacciCount, int sortingRounds) {
        logger.info("Starting CPU load operations - Fibonacci calculations: {}, Sorting rounds: {}", 
                   fibonacciCount, sortingRounds);
    }

    public void logFibonacciResult(int fibNumber, long result) {
        logger.debug("Fibonacci({}) = {}", fibNumber, result);
    }

    public void logCpuLoadComplete() {
        logger.info("CPU load operations completed");
    }

    // Memory Load Logging
    public void logMemoryLoadStart(int totalMB) {
        logger.info("Starting memory allocation - Total memory to allocate: {}MB", totalMB);
    }

    public void logMemoryLoadComplete(int totalMB) {
        logger.info("Memory allocation completed - {}MB allocated and processed", totalMB);
    }

    public void logStringOperationResult(int stringLength) {
        logger.debug("Large string created with length: {}", stringLength);
    }

    public void logMemoryError() {
        logger.error("Out of memory during memory load test - continuing with reduced allocation");
    }

    // Database Load Logging
    public void logDbWriteStart(int operations, int recordsPerBatch) {
        logger.info("Starting database write operations - {} batches with {} records each", 
                   operations, recordsPerBatch);
    }

    public void logDbWriteBatch(int batchNum, int recordsInserted) {
        logger.debug("Database write batch {} completed - {} records inserted", batchNum, recordsInserted);
    }

    public void logDbWriteComplete(int operations, int totalRecords) {
        logger.info("Database write operations completed - {} batches, {} total records inserted", 
                   operations, totalRecords);
    }

    public void logDbReadStart(int operations) {
        logger.info("Starting optimized database read operations - {} read queries planned", operations);
    }

    public void logDbReadOperation(int operationNum, int recordsFound, int limit) {
        logger.debug("Database read operation {} completed - {} records found (limit: {})", 
                    operationNum, recordsFound, limit);
    }

    public void logDbReadAnalysis(long uniqueIps) {
        logger.debug("Read operation analysis - Unique IPs found: {}", uniqueIps);
    }

    public void logDbReadComplete(int operations) {
        logger.info("Database read operations completed - {} queries executed", operations);
    }

    // Database Cleanup Logging
    public void logCleanupStart() {
        logger.info("Starting database cleanup operation");
    }

    public void logCleanupAnalysis(long totalRecords, int keepThreshold) {
        logger.info("Cleanup analysis - Total records: {}, Keep threshold: {}", 
                   totalRecords, keepThreshold);
    }

    public void logCleanupNotNeeded(long currentRecords, int keepThreshold) {
        logger.info("Database cleanup skipped - Current records ({}) <= threshold ({})", 
                   currentRecords, keepThreshold);
    }

    public void logCleanupComplete(int deletedRecords, long remainingRecords, int keepThreshold) {
        logger.info("Database cleanup completed - Deleted: {}, Remaining: {}, Keep threshold: {}", 
                   deletedRecords, remainingRecords, keepThreshold);
    }

    public void logCleanupError(String errorMessage) {
        logger.error("Database cleanup failed: {}", errorMessage);
    }

    // Delay Load Logging
    public void logDelayLoadStart(int externalCalls, int delayMs) {
        logger.info("Starting delay load operations - {} external calls with {}ms delay each", 
                   externalCalls, delayMs);
    }

    public void logExternalCall(int callNumber) {
        logger.debug("Simulating external service call {}", callNumber);
    }

    public void logDelayLoadComplete(int externalCalls, int mathOperations) {
        logger.info("Delay load operations completed - {} external calls, {} math operations", 
                   externalCalls, mathOperations);
    }

    // General Logging Methods
    public void logInfo(String message, Object... args) {
        logger.info(message, args);
    }

    public void logDebug(String message, Object... args) {
        logger.debug(message, args);
    }

    public void logWarn(String message, Object... args) {
        logger.warn(message, args);
    }

    public void logError(String message, Object... args) {
        logger.error(message, args);
    }

    public void logError(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}