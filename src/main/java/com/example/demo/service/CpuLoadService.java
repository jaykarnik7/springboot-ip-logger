package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CpuLoadService {

    @Autowired
    private LoggingService loggingService;

    private Random random = new Random();

    // CPU Configuration
    @Value("${app.load.cpu.fibonacci.count:5}")
    private int fibonacciCount;

    @Value("${app.load.cpu.fibonacci.base:35}")
    private int fibonacciBase;

    @Value("${app.load.cpu.sorting.rounds:3}")
    private int sortingRounds;

    @Value("${app.load.cpu.sorting.array-size:50000}")
    private int sortingArraySize;

    public void performCpuLoad() {
        loggingService.logCpuLoadStart(fibonacciCount, sortingRounds);
        performConfigurableCpuTask();
        loggingService.logCpuLoadComplete();
    }

    /**
     * CONFIGURABLE CPU-INTENSIVE TASK
     */
    private void performConfigurableCpuTask() {
        // Calculate configurable number of Fibonacci numbers
        for (int i = 0; i < fibonacciCount; i++) {
            int fibNumber = fibonacciBase + i;
            long result = calculateFibonacci(fibNumber);
            loggingService.logFibonacciResult(fibNumber, result);
        }

        // Additional CPU work: Sort large arrays
        for (int i = 0; i < sortingRounds; i++) {
            performConfigurableSortingWork();
        }
    }

    /**
     * Configurable sorting work
     */
    private void performConfigurableSortingWork() {
        List<Integer> numbers = new ArrayList<>();
        // Create a configurable-sized list of random numbers
        for (int i = 0; i < sortingArraySize; i++) {
            numbers.add(random.nextInt(1000000));
        }
        // Sort it (bubble sort for more CPU usage)
        bubbleSort(new ArrayList<>(numbers));
    }

    /**
     * Recursive Fibonacci calculation (intentionally inefficient for CPU load)
     */
    private long calculateFibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return calculateFibonacci(n - 1) + calculateFibonacci(n - 2);
    }

    /**
     * Inefficient bubble sort for CPU consumption
     */
    private void bubbleSort(List<Integer> arr) {
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    // Swap
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
    }
}