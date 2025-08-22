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

# Enable multiple load types
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true&enableMemory=true&enableDbWrites=true"

# Enable all load types (stress test)
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true"

# NEW: Enable database cleanup
curl -X POST "http://localhost:8080/greet?name=Jay&enableCleanup=true"

# Combined load testing with cleanup
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true&enableCleanup=true"

# Specific load combinations for targeted testing
curl -X POST "http://localhost:8080/greet?name=Jay&enableMemory=true&enableDbReads=true"
```

### Load Control Parameters
- `enableCpu` (default: false) - Enable CPU-intensive operations
- `enableMemory` (default: false) - Enable memory allocation and string operations  
- `enableDbWrites` (default: false) - Enable additional database write operations
- `enableDbReads` (default: false) - Enable optimized database read operations
- `enableDelays` (default: false) - Enable simulated processing delays
- `enableCleanup` (default: false) - **NEW**: Enable manual database cleanup

## Configuration

### Load Testing Configuration
The intensity of each load type can be configured in `application.properties`:

#### CPU Load Settings
```properties
app.load.cpu.fibonacci.count=5              # Number of Fibonacci calculations
app.load.cpu.fibonacci.base=35              # Base number for Fibonacci sequence
app.load.cpu.sorting.rounds=3               # Number of sorting operations
app.load.cpu.sorting.array-size=50000       # Size of arrays to sort
```

#### Memory Load Settings
```properties
app.load.memory.chunks=100                  # Number of memory chunks to allocate
app.load.memory.chunk-size-mb=1             # Size of each chunk in MB
app.load.memory.string-operations=100000    # Number of string concatenation operations
app.load.memory.hold-time-ms=500            # Time to hold memory allocation
```

#### Database Load Settings
```properties
app.load.db.write.extra-entries=10          # Additional database write operations
app.load.db.write.delay-ms=10               # Delay between write operations
app.load.db.read.operations=20              # Number of read operations
app.load.db.read.delay-ms=50                # Delay between read operations
app.load.db.read.fetch-count=100            # NEW: Number of recent records to fetch (replaces full scan)
```

#### Processing Delay Settings
```properties
app.load.delay.external-calls=3             # Number of simulated external calls
app.load.delay.external-call-ms=200         # Delay per external call
app.load.delay.math-operations=1000000      # Number of mathematical operations
```

### NEW: Database Cleanup Settings
```properties
app.db.cleanup.keep-records=1000            # Number of records to keep during cleanup
app.db.cleanup.auto-threshold=10000         # Auto-cleanup when total records exceed this
```

### Pre-configured Load Presets
The application includes several preset configurations for different testing scenarios:

- **PRESET 1: LIGHT LOAD** - Good for development/testing
- **PRESET 2: MEDIUM LOAD** - Default configuration  
- **PRESET 3: HEAVY LOAD** - Significant stress on t3.micro instances
- **PRESET 4: EXTREME LOAD** - May cause timeouts/OOM errors
- **PRESET 5-7: ISOLATED TESTING** - CPU-only, Memory-only, or Database-only loads
- **PRESET 8: BOTTLENECK TESTING** - Minimal resources with heavy load

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://your-rds-endpoint:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
```

## Performance Optimizations

### Database Optimizations
- **Indexed Queries**: The application uses indexed queries for timestamp-based operations
- **Limited Fetching**: Database reads now fetch only a configurable number of recent records
- **Removed Full Scans**: Eliminated full table scan operations that were causing performance issues
- **Automatic Cleanup**: Prevents database bloat through configurable retention policies

### Recommended Database Indexes
Ensure these indexes are created for optimal performance:
```sql
CREATE INDEX idx_ip_log_timestamp ON ip_log(timestamp DESC);
CREATE INDEX idx_ip_log_name ON ip_log(name);
```

## Infrastructure

### Database
- **Type**: Amazon RDS PostgreSQL
- **Purpose**: Stores IP logging data and serves as target for database load testing
- **Schema**: Auto-generated via JPA/Hibernate
- **Maintenance**: Automatic cleanup prevents unlimited growth

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
3. Create recommended indexes:
   ```sql
   CREATE INDEX idx_ip_log_timestamp ON ip_log(timestamp DESC);
   CREATE INDEX idx_ip_log_name ON ip_log(name);
   ```
4. Install dependencies:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   ./mvnw spring-boot:run
   # or
   mvn spring-boot:run
   ```

### Testing

#### Basic Functionality Test
```bash
curl -X POST "http://localhost:8080/greet?name=TestUser"
```

#### Load Testing Examples
```bash
# CPU load test
curl -X POST "http://localhost:8080/greet?name=CPUTest&enableCpu=true"

# Optimized database read test (no more freezing!)
curl -X POST "http://localhost:8080/greet?name=DBReadTest&enableDbReads=true"

# Database cleanup test
curl -X POST "http://localhost:8080/greet?name=CleanupTest&enableCleanup=true"

# Combined stress test
curl -X POST "http://localhost:8080/greet?name=StressTest&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true"
```

#### Database Management Testing
```bash
# Test automatic cleanup by generating many records
for i in {1..50}; do
  curl -X POST "http://localhost:8080/greet?name=TestUser$i&enableDbWrites=true"
done

# Manual cleanup
curl -X POST "http://localhost:8080/greet?name=ManualCleanup&enableCleanup=true"
```

### Health Check
The application includes Spring Boot Actuator for health monitoring:
```bash
curl http://localhost:8080/actuator/health
```

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
sudo journalctl -u springboot-app | grep -i 'cleanup'    # Check cleanup logs
```

## Use Cases

### Performance Testing
- **Baseline Testing**: Call API without load parameters to establish baseline performance
- **Resource-Specific Testing**: Enable individual load types to identify bottlenecks
- **Stress Testing**: Enable all load types to test system limits
- **Scalability Testing**: Gradually increase concurrent requests with various load combinations
- **Database Performance**: Test optimized read operations without full table scans

### Database Management
- **Growth Testing**: Generate large numbers of records to test cleanup mechanisms
- **Retention Testing**: Validate that cleanup maintains only the specified number of recent records
- **Performance Validation**: Confirm that optimized reads perform well even with large datasets

### Monitoring Validation
- **APM Testing**: Validate New Relic monitoring accuracy under different load conditions
- **Alert Testing**: Trigger specific resource usage patterns to test monitoring alerts
- **Dashboard Validation**: Generate predictable load patterns for dashboard verification
- **Database Metrics**: Monitor database performance improvements with optimized queries

### Infrastructure Testing
- **Database Performance**: Test RDS PostgreSQL performance under various read/write loads
- **EC2 Capacity Planning**: Understand t3.micro instance limits under different workloads
- **Network Testing**: Validate application behavior under simulated network delays
- **Resource Management**: Test automatic cleanup and resource optimization

## Troubleshooting

### Common Issues and Solutions

#### Database Performance Issues
- **Problem**: Slow database reads or writes
- **Solution**: Ensure indexes are created, check cleanup configuration
- **Commands**:
  ```sql
  -- Check if indexes exist
  \d ip_log
  
  -- Create missing indexes
  CREATE INDEX IF NOT EXISTS idx_ip_log_timestamp ON ip_log(timestamp DESC);
  CREATE INDEX IF NOT EXISTS idx_ip_log_name ON ip_log(name);
  
  -- Check table size
  SELECT COUNT(*) FROM ip_log;
  ```

#### Application Freezing on Database Reads
- **Problem**: Application freezes when using `enableDbReads=true`
- **Solution**: This has been fixed by replacing full table scans with limited record fetching
- **Configuration**: Adjust `app.load.db.read.fetch-count` to control how many records are fetched

#### Database Growing Too Large
- **Problem**: Database becomes too large and affects performance
- **Solution**: Use the new cleanup functionality
- **Manual Cleanup**:
  ```bash
  curl -X POST "http://localhost:8080/greet?name=Cleanup&enableCleanup=true"
  ```
- **Auto Cleanup**: Configure `app.db.cleanup.auto-threshold` for automatic maintenance

### Configuration Tuning

#### For High-Volume Testing
```properties
# More frequent cleanup
app.db.cleanup.keep-records=500
app.db.cleanup.auto-threshold=2000

# Smaller read batches for faster response
app.load.db.read.fetch-count=50
app.load.db.read.operations=10
```

#### For Development
```properties
# Less aggressive cleanup
app.db.cleanup.keep-records=2000
app.db.cleanup.auto-threshold=10000

# Larger batches for more realistic testing
app.load.db.read.fetch-count=200
```

## Migration Notes

### Changes from Previous Version
1. **Database Reads**: Full table scans removed, replaced with limited record fetching
2. **New Parameter**: `enableCleanup` parameter added for manual database cleanup
3. **Auto Cleanup**: Automatic cleanup runs when record threshold is exceeded
4. **New Configuration**: Added database cleanup and read fetch count settings
5. **Performance**: Significantly improved performance for database read operations

### Updating Existing Installations
1. Update application.properties with new configuration options
2. Deploy updated code
3. Test optimized database reads: `curl -X POST "http://localhost:8080/greet?name=Test&enableDbReads=true"`
4. Test cleanup functionality: `curl -X POST "http://localhost:8080/greet?name=Test&enableCleanup=true"`

## License

MIT