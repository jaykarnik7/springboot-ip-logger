package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CpuService {

    private final Random random = new Random();

    // CPU Configuration
    @Value("${app.load.cpu.fibonacci.count:5}")
    private int fibonacciCount;
    
    @Value("${app.load.cpu.fibonacci.base:35}")
    private int fibonacciBase;
    
    @Value("${app.load.cpu.sorting.rounds:3}")
    private int sortingRounds;
    
    @Value("${app.load.cpu.sorting.array-size:50000}")
    private int sortingArraySize;

    /**
     * CONFIGURABLE CPU-INTENSIVE TASK
     */
    public void performConfigurableCpuTask() {
        System.out.println("Starting CPU-intensive operations (Fibonacci:" + fibonacciCount + 
                         ", Sorting rounds:" + sortingRounds + ")...");
        
        // Calculate configurable number of Fibonacci numbers
        for (int i = 0; i < fibonacciCount; i++) {
            int fibNumber = fibonacciBase + i;
            long result = calculateFibonacci(fibNumber);
            System.out.println("Fibonacci(" + fibNumber + ") = " + result);
        }
        
        // Additional CPU work: Sort large arrays
        for (int i = 0; i < sortingRounds; i++) {
            performConfigurableSortingWork(i + 1);
        }
        
        System.out.println("CPU-intensive operations completed");
    }

    /**
     * Configurable sorting work
     */
    private void performConfigurableSortingWork(int round) {
        System.out.println("Starting sorting round " + round + " with array size: " + sortingArraySize);
        
        List<Integer> numbers = new ArrayList<>();
        // Create a configurable-sized list of random numbers
        for (int i = 0; i < sortingArraySize; i++) {
            numbers.add(random.nextInt(1000000));
        }
        
        long startTime = System.currentTimeMillis();
        // Sort it (bubble sort for more CPU usage)
        bubbleSort(new ArrayList<>(numbers));
        long endTime = System.currentTimeMillis();
        
        System.out.println("Sorting round " + round + " completed in " + (endTime - startTime) + "ms");
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