package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class IpLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String ip;
    private LocalDateTime timestamp;
    // getters/setters omitted for brevity


public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getIp() {
    return ip;
}

public void setIp(String ip) {
    this.ip = ip;
}

public LocalDateTime getTimestamp() {
    return timestamp;
}

public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
}
)
