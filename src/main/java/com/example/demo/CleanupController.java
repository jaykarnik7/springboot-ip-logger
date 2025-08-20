package com.example.demo;

import com.example.demo.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CleanupController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/cleanup")
    public String cleanupDatabase() {
        System.out.println("Database cleanup requested via API...");
        
        try {
            int deletedCount = databaseService.cleanDatabase();
            String stats = databaseService.getDatabaseStats();
            
            return String.format(
                "✅ Database cleanup completed successfully!%n" +
                "🗑️ Entries deleted: %d%n" +
                "📊 %s%n" +
                "ℹ️ Only the last 10 entries have been preserved.%n",
                deletedCount, stats
            );
            
        } catch (Exception e) {
            System.err.println("Database cleanup failed: " + e.getMessage());
            return String.format(
                "❌ Database cleanup failed: %s%n" +
                "Please check the server logs for more details.%n",
                e.getMessage()
            );
        }
    }
}