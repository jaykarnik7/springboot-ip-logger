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
}