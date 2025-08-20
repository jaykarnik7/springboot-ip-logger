package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DelayService {

    // Processing Delay Configuration
    @Value("${app.load.delay.external-calls:3}")
    private int externalServiceCalls;
    
    @Value("${app.load.delay.external-call-ms:200}")
    private int externalCallDelay;
    
    @Value("${app.load.delay.math-operations:1000000}")
    private int mathOperations;

    /**
     * CONFIGURABLE SLOW PROCESSING
     */
    public void simulateConfigurableSlowProcessing() {
        System.out.println("Starting processing delays - " + externalServiceCalls + " external calls with " + 
                         externalCallDelay + "ms delay each, plus " + mathOperations + " math operations...");
        
        try {
            // Simulate configurable number of external service calls
            for (int i = 0; i < externalServiceCalls; i++) {
                System.out.println("Simulating external service call " + (i + 1) + "/" + externalServiceCalls + "...");
                Thread.sleep(externalCallDelay);
            }
            
            // Simulate configurable complex calculations
            System.out.println("Starting " + mathOperations + " mathematical operations...");
            double total = 0;
            
            for (int i = 0; i < mathOperations; i++) {
                total += Math.sin(i) * Math.cos(i) * Math.tan(i);
                
                // Progress reporting for large operations
                if (mathOperations > 100000 && (i + 1) % (mathOperations / 10) == 0) {
                    System.out.println("Completed " + (i + 1) + "/" + mathOperations + " math operations");
                }
            }
            
            // Use the result to prevent optimization
            System.out.println("Mathematical operations completed. Final calculation result: " + 
                             String.format("%.6f", total));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Processing delay simulation interrupted");
        }
        
        System.out.println("Slow processing simulation completed: " + 
                          externalServiceCalls + " external calls, " + 
                          mathOperations + " math operations");
    }

    /**
     * Simulate a single external service call (for testing individual calls)
     */
    public void simulateSingleExternalCall(String callName) {
        try {
            System.out.println("Simulating external call: " + callName + " (" + externalCallDelay + "ms)");
            Thread.sleep(externalCallDelay);
            System.out.println("External call completed: " + callName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("External call interrupted: " + callName);
        }
    }

    /**
     * Get processing delay configuration info
     */
    public String getDelayConfig() {
        return String.format("Delay Config - External calls: %d x %dms, Math operations: %d",
                           externalServiceCalls, externalCallDelay, mathOperations);
    }
}