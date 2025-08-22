package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface IpLogRepository extends JpaRepository<IpLog, Long> {

    List<IpLog> findTop2ByOrderByTimestampDesc();

    // Method to find last N entries ordered by timestamp
    @Query(value = "SELECT * FROM ip_log ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<IpLog> findTopNByOrderByTimestampDesc(@Param("limit") int limit);

    // Method to delete all records except the last N entries
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ip_log WHERE id NOT IN " +
            "(SELECT id FROM (SELECT id FROM ip_log ORDER BY timestamp DESC LIMIT :keepCount) AS temp)", nativeQuery = true)
    int deleteAllExceptLastN(@Param("keepCount") int keepCount);

    // Method to count total records (for cleanup logging)
    @Query("SELECT COUNT(i) FROM IpLog i")
    long countTotalRecords();
}