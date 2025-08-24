# Spring Boot Load Testing Application

This is a comprehensive Spring Boot application designed for performance monitoring and load testing with New Relic integration. The application provides configurable load generation across multiple system resources with optimized database operations and automatic cleanup functionality.

## Features

### Core Functionality
- **IP Logging**: Logs client IP, name, and timestamp to PostgreSQL database
- **Formatted Response**: Returns current time and last request information in a user-friendly format
- **Load Testing**: Configurable load generation across CPU, Memory, Database, and Network delays
- **Database Cleanup**: Automatic and manual database cleanup to manage record growth
- **Optimized Database Reads**: Efficient database read operations without full table scans
- **New Relic Integration**: Comprehensive APM monitoring with agent integration
- **CI/CD Pipeline**: Automated deployment to EC2 with GitHub Actions

### Load Testing Capabilities
The application can generate controlled load across five different system resources:

1. **CPU Load**: Fibonacci calculations and bubble sort operations
2. **Memory Load**: Dynamic memory allocation with configurable retention
3. **Database Writes**: Additional database write operations to RDS PostgreSQL
4. **Optimized Database Reads**: Efficient read operations fetching only recent records
5. **Processing Delays**: Simulated external service calls and mathematical operations

### NEW: Database Management
- **Automatic Cleanup**: Automatically purges old records when threshold is exceeded
- **Manual Cleanup**: On-demand cleanup via API parameter
- **Configurable Retention**: Keep only the most recent N records as configured

## API Usage

### Basic Usage
```bash
curl -X POST "http://localhost:8080/greet?name=Jay"
```

### Load Testing Usage
All load types are **disabled by default**. Enable specific load types using boolean parameters:

```bash
# Enable CPU load only
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true"

# Enable all load types (stress test)
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true"

# NEW: Enable database cleanup
curl -X POST "http://localhost:8080/greet?name=Jay&enableCleanup=true"

# Specific load combinations for targeted testing
curl -X POST "http://localhost:8080/greet?name=Jay&enableMemory=true&enableDbReads=true"
```

### Load Control Parameters
- `enableCpu` (default: false) - Enable CPU-intensive operations
- `enableMemory` (default: false) - Enable memory allocation and string operations  
- `enableDbWrites` (default: false) - Enable additional database write operations
- `enableDbReads` (default: false) - Enable optimized database read operations
- `enableDelays` (default: false) - Enable simulated processing delays
- `enableCleanup` (default: false) - Enable manual database cleanup

