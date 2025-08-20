package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface IpLogRepository extends JpaRepository<IpLog, Long> {
    
    // Existing method
    List<IpLog> findTop2ByOrderByTimestampDesc();
    
    // New methods for enhanced functionality
    
    /**
     * Find top 10 entries for cleanup operations
     */
    List<IpLog> findTop10ByOrderByTimestampDesc();
    
    /**
     * Find configurable number of top entries by timestamp
     */
    @Query("SELECT il FROM IpLog il ORDER BY il.timestamp DESC LIMIT :count")
    List<IpLog> findTopByOrderByTimestampDesc(@Param("count") int count);
    
    /**
     * Delete entries older than specified timestamp
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM IpLog il WHERE il.timestamp < :cutoffTimestamp")
    int deleteByTimestampBefore(@Param("cutoffTimestamp") LocalDateTime cutoffTimestamp);
    
    /**
     * Get count of entries newer than specified timestamp
     */
    @Query("SELECT COUNT(il) FROM IpLog il WHERE il.timestamp >= :timestamp")
    long countByTimestampAfter(@Param("timestamp") LocalDateTime timestamp);
    
    /**
     * Find entries by name pattern (useful for finding test entries)
     */
    List<IpLog> findByNameContainingOrderByTimestampDesc(String namePattern);
}