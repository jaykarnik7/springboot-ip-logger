package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IpLogRepository extends JpaRepository<IpLog, Long> {
    List<IpLog> findTop2ByOrderByTimestampDesc();
}