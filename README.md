# Spring Boot Load Testing Application - Enhanced Version

This is a comprehensive Spring Boot application designed for performance monitoring and load testing with New Relic integration. The application provides configurable load generation across multiple system resources with enhanced database operations and connection pool testing capabilities.

## 🆕 Recent Enhancements

### New Features
- **Database Cleanup Endpoint**: `/cleanup` - Purges database keeping only last 10 entries
- **Concurrent Database Operations**: Multiple connection testing for both reads and writes
- **Modular Architecture**: Services separated into dedicated classes
- **Enhanced Database Operations**: Optimized reads without full table scans
- **Connection Pool Saturation Testing**: Configurable concurrent database connections
- **Consistent Operation Counts**: Single `x` parameter for both reads and writes

### Architecture Improvements
- **Service Layer**: Separated load operations into dedicated service classes
- **Enhanced Repository**: Added custom queries for cleanup and optimized operations  
- **Better Logging**: Detailed progress reporting and execution timing
- **Memory Management**: Improved memory allocation tracking and statistics

## Features

### Core Functionality
- **IP Logging**: Logs client IP, name, and timestamp to PostgreSQL database
- **Formatted Response**: Returns current time and last request information in a user-friendly format
- **Load Testing**: Configurable load generation across CPU, Memory, Database, and Network delays
- **New Relic Integration**: Comprehensive APM monitoring with agent integration
- **CI/CD Pipeline**: Automated deployment to EC2 with GitHub Actions
- **Database Cleanup**: Manual cleanup endpoint to manage database size

### Load Testing Capabilities
The application can generate controlled load across five different system resources:

1. **CPU Load**: Fibonacci calculations and bubble sort operations
2. **Memory Load**: Dynamic memory allocation with configurable retention
3. **Database Writes**: Concurrent database write operations with multiple connections
4. **Database Reads**: Concurrent database read operations (last X entries only)
5. **Processing Delays**: Simulated external service calls and mathematical operations

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

# Enable database operations (both reads and writes)
curl -X POST "http://localhost:8080/greet?name=Jay&enableDbWrites=true&enableDbReads=true"

# Enable all load types (full stress test)
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true"

# Connection pool saturation test
curl -X POST "http://localhost:8080/greet?name=PoolTest&enableDbWrites=true&enableDbReads=true"
```

### Database Cleanup
```bash
# Clean database - keeps only last 10 entries
curl -X POST "http://localhost:8080/cleanup"
```

### Load Control Parameters
- `enableCpu` (default: false) - Enable CPU-intensive operations
- `enableMemory` (default: false) - Enable memory allocation and string operations  
- `enableDbWrites` (default: false) - Enable concurrent database write operations
- `enableDbReads` (default: false) - Enable concurrent database read operations
- `enableDelays` (default: false) - Enable simulated processing delays

## Configuration

### Enhanced Load Testing Configuration
The intensity of each load type can be configured in `application.properties`:

#### Consistent Operation Count
```properties
app.load.db.operations.count=15             # Used for both reads and writes (the 'x' value)
```

#### CPU Load Settings
```properties
app.load.cpu.fibonacci.count=5              # Number of Fibonacci calculations
app.load.cpu.fibonacci.base=30              # Base number for Fibonacci sequence
app.load.cpu.sorting.rounds=3               # Number of sorting operations
app.load.cpu.sorting.array-size=10000       # Size of arrays to sort
```

#### Memory Load Settings
```properties
app.load.memory.chunks=50                   # Number of memory chunks to allocate
app.load.memory.chunk-size-mb=2             # Size of each chunk in MB
app.load.memory.string-operations=50000     # Number of string concatenation operations
app.load.memory.hold-time-ms=1000           # Time to hold memory allocation
```

#### Enhanced Database Load Settings
```properties
app.load.db.write.delay-ms=25               # Delay between write operations
app.load.db.read.delay-ms=50                # Delay between read operations
app.load.db.connection-pool-threads=8       # Number of concurrent connections to use
```

#### Processing Delay Settings
```properties
app.load.delay.external-calls=3             # Number of simulated external calls
app.load.delay.external-call-ms=300         # Delay per external call
app.load.delay.math-operations=500000       # Number of mathematical operations
```

#### Connection Pool Configuration
```properties
spring.datasource.hikari.maximum-pool-size=10    # Maximum connections
spring.datasource.hikari.minimum-idle=2          # Minimum idle connections
spring.datasource.hikari.leak-detection-threshold=30000  # Leak detection
```

### New Configuration Presets
The application includes several enhanced preset configurations:

- **PRESET 1: LIGHT LOAD** - Development/testing (5 operations, 3 pool threads)
- **PRESET 2: MEDIUM LOAD** - Default configuration (15 operations, 8 pool threads)
- **PRESET 3: HEAVY LOAD** - System stress (25 operations, 15 pool threads)
- **PRESET 4: EXTREME LOAD** - May cause timeouts (50 operations, 25 pool threads)
- **PRESET 5: CONNECTION POOL SATURATION** - Tests pool limits (30 operations, 20 threads, 5 max pool)
- **PRESET 6: DATABASE INTENSIVE** - Database-only testing (40 operations, 15 threads)
- **PRESET 7-8: ISOLATED TESTING** - Memory-only or CPU-only loads

## Service Architecture

### Service Classes
The application is now organized into dedicated service classes:

#### `DatabaseService`
- Handles all database operations (reads/writes)
- Manages concurrent connections
- Provides database cleanup functionality
- Returns database statistics

#### `CpuService`
- Manages CPU-intensive operations
- Fibonacci calculations and sorting algorithms
- Progress reporting for long operations

#### `MemoryService`
- Controls memory allocation and string operations
- Memory usage statistics and monitoring
- Configurable memory retention

#### `DelayService`
- Simulates external service calls
- Mathematical processing operations
- Configurable delay patterns

### Repository Enhancements
New `IpLogRepository` methods:
- `findTop10ByOrderByTimestampDesc()` - For cleanup operations
- `findTopByOrderByTimestampDesc(int count)` - Configurable recent entries
- `deleteByTimestampBefore(LocalDateTime)` - Cleanup old entries
- `countByTimestampAfter(LocalDateTime)` - Statistics

## Database Operations

### Enhanced Database Testing
- **Concurrent Writes**: Multiple threads writing simultaneously to test connection pool
- **Concurrent Reads**: Multiple threads reading recent entries (no full table scans)
- **Consistent Operations**: Same count (x) used for both reads and writes
- **Connection Pool Testing**: Configurable number of concurrent connections
- **Optimized Queries**: Uses indexed queries for better performance

### Database Cleanup
The `/cleanup` endpoint:
- Keeps only the last 10 database entries
- Deletes all older entries efficiently
- Returns statistics about cleanup operation
- Uses transaction management for consistency

### Database Indexes
Make sure these indexes exist for optimal performance:
```sql
CREATE INDEX idx_ip_log_timestamp ON ip_log(timestamp DESC);
CREATE INDEX idx_ip_log_name ON ip_log(name);
```

## Infrastructure

### Database
- **Type**: Amazon RDS PostgreSQL
- **Purpose**: Stores IP logging data and serves as target for concurrent load testing
- **Schema**: Auto-generated via JPA/Hibernate
- **Connection Pool**: HikariCP with configurable pool size

### Deployment
- **Platform**: Amazon EC2 (optimized for t3.micro instances)
- **Service Management**: systemd service with automatic startup
- **Monitoring**: New Relic APM agent integration
- **CI/CD**: GitHub Actions with automated deployment pipeline

### New Relic Integration
The application includes comprehensive New Relic monitoring:
- **Agent Path**: `/opt/newrelic/newrelic.jar`
- **Configuration**: `/opt/newrelic/newrelic.yml`
- **JVM Arguments**: `-javaagent:/opt/newrelic/newrelic.jar`

## Local Development

### Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL database (local or RDS)

### Setup
1. Clone the repository
2. Configure database connection in `application.properties`
3. Install dependencies:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   # or
   mvn spring-boot:run
   ```

### Testing Examples
```bash
# Basic functionality test
curl -X POST "http://localhost:8080/greet?name=TestUser"

# Connection pool saturation test
curl -X POST "http://localhost:8080/greet?name=PoolTest&enableDbWrites=true&enableDbReads=true"

# Full system stress test
curl -X POST "http://localhost:8080/greet?name=StressTest&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true"

# Database cleanup
curl -X POST "http://localhost:8080/cleanup"
```

### Health Check
The application includes Spring Boot Actuator for health monitoring:
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus
```

## Performance Testing Scenarios

### Connection Pool Testing
1. **Pool Saturation**: Set high concurrent threads with small pool size
2. **Pool Efficiency**: Test optimal pool size vs. concurrent operations
3. **Leak Detection**: Monitor for connection leaks under load

### Database Performance Testing
1. **Concurrent Writes**: Test database write performance under concurrent load
2. **Read Performance**: Test indexed read performance with multiple connections
3. **Mixed Workload**: Combine reads and writes to simulate real-world usage

### System Resource Testing
1. **CPU Bound**: Test CPU performance with mathematical operations
2. **Memory Bound**: Test memory allocation and garbage collection
3. **I/O Bound**: Test database and simulated network operations

## Deployment Pipeline

The GitHub Actions workflow automatically:
1. Builds the application with Maven
2. Copies JAR file to EC2 instance
3. Creates/updates systemd service with New Relic integration
4. Performs health checks and validation
5. Provides deployment status and management commands

### Useful Management Commands
```bash
# Service management
sudo systemctl status springboot-app     # Check status
sudo systemctl restart springboot-app    # Restart service
sudo systemctl stop springboot-app       # Stop service

# Log monitoring
sudo journalctl -u springboot-app -f     # Follow logs
sudo journalctl -u springboot-app | grep -i 'new relic'  # Check New Relic logs
sudo journalctl -u springboot-app | grep -i 'database'   # Check database logs
```

## Use Cases

### Enhanced Performance Testing
- **Connection Pool Validation**: Test connection pool behavior under various loads
- **Database Concurrency**: Validate database performance with concurrent operations
- **Resource Isolation**: Test individual system components in isolation
- **Cleanup Testing**: Validate database maintenance operations

### Monitoring and Alerting
- **APM Validation**: Test New Relic monitoring under various load conditions
- **Database Monitoring**: Monitor connection pool metrics and query performance
- **Resource Monitoring**: Track CPU, memory, and database resource usage
- **Alert Testing**: Trigger specific resource usage patterns for alert validation

### Infrastructure Testing
- **Scalability Testing**: Understand system limits with concurrent operations
- **Database Optimization**: Test query performance and index effectiveness
- **Connection Management**: Validate connection pool configuration
- **Cleanup Operations**: Test database maintenance procedures

## Troubleshooting

### Common Issues
1. **Connection Pool Exhaustion**: Reduce `connection-pool-threads` or increase `maximum-pool-size`
2. **High Memory Usage**: Reduce `memory.chunks` or `chunk-size-mb`
3. **Slow Database Operations**: Check indexes and reduce operation delays
4. **Application Timeouts**: Reduce operation counts or increase timeout values

### Monitoring
- Check connection pool metrics in New Relic
- Monitor database query performance
- Track memory usage patterns
- Review application logs for errors

## License

MIT