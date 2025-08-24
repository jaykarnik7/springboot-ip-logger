package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DelayLoadService {

    // Processing Delay Configuration
    @Value("${app.load.delay.external-calls:3}")
    private int externalServiceCalls;

    @Value("${app.load.delay.external-call-ms:200}")
    private int externalCallDelay;

    @Value("${app.load.delay.math-operations:1000000}")
    private int mathOperations;

    public void performDelayLoad() {
        System.out.println("Simulating " + externalServiceCalls + " external calls with " +
                externalCallDelay + "ms delay each...");
        simulateConfigurableSlowProcessing();
    }

    /**
     * CONFIGURABLE SLOW PROCESSING
     */
    @SuppressWarnings("unused")
    private void simulateConfigurableSlowProcessing() {
        try {
            // Simulate configurable number of external service calls
            for (int i = 0; i < externalServiceCalls; i++) {
                System.out.println("Simulating external service call " + (i + 1) + "...");
                Thread.sleep(externalCallDelay);
            }

            // Simulate configurable complex calculations
            double total = 0;
            for (int i = 0; i < mathOperations; i++) {
                total += Math.sin(i) * Math.cos(i) * Math.tan(i);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Slow processing simulation completed: " +
                externalServiceCalls + " external calls, " +
                mathOperations + " math operations");
    }
}