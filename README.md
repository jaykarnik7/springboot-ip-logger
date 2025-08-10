# Spring Boot Load Testing Application

This is a comprehensive Spring Boot application designed for performance monitoring and load testing with New Relic integration. The application provides configurable load generation across multiple system resources.

## Features

### Core Functionality
- **IP Logging**: Logs client IP, name, and timestamp to PostgreSQL database
- **Formatted Response**: Returns current time and last request information in a user-friendly format
- **Load Testing**: Configurable load generation across CPU, Memory, Database, and Network delays
- **New Relic Integration**: Comprehensive APM monitoring with agent integration
- **CI/CD Pipeline**: Automated deployment to EC2 with GitHub Actions

### Load Testing Capabilities
The application can generate controlled load across five different system resources:

1. **CPU Load**: Fibonacci calculations and bubble sort operations
2. **Memory Load**: Dynamic memory allocation with configurable retention
3. **Database Writes**: Additional database write operations to RDS PostgreSQL
4. **Database Reads**: Multiple read operations including full table scans
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

# Enable multiple load types
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true&enableMemory=true&enableDbWrites=true"

# Enable all load types (stress test)
curl -X POST "http://localhost:8080/greet?name=Jay&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true"

# Specific load combinations for targeted testing
curl -X POST "http://localhost:8080/greet?name=Jay&enableMemory=true&enableDbReads=true"
```

### Load Control Parameters
- `enableCpu` (default: false) - Enable CPU-intensive operations
- `enableMemory` (default: false) - Enable memory allocation and string operations  
- `enableDbWrites` (default: false) - Enable additional database write operations
- `enableDbReads` (default: false) - Enable intensive database read operations
- `enableDelays` (default: false) - Enable simulated processing delays

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
app.load.db.read.full-scan-interval=5       # Interval for full table scans
app.load.db.read.delay-ms=50                # Delay between read operations
```

#### Processing Delay Settings
```properties
app.load.delay.external-calls=3             # Number of simulated external calls
app.load.delay.external-call-ms=200         # Delay per external call
app.load.delay.math-operations=1000000      # Number of mathematical operations
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

## Infrastructure

### Database
- **Type**: Amazon RDS PostgreSQL
- **Purpose**: Stores IP logging data and serves as target for database load testing
- **Schema**: Auto-generated via JPA/Hibernate

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

### Testing
```bash
# Basic functionality test
curl -X POST "http://localhost:8080/greet?name=TestUser"

# Load testing examples
curl -X POST "http://localhost:8080/greet?name=LoadTest&enableCpu=true"
curl -X POST "http://localhost:8080/greet?name=StressTest&enableCpu=true&enableMemory=true&enableDbWrites=true&enableDbReads=true&enableDelays=true"
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
```

## Use Cases

### Performance Testing
- **Baseline Testing**: Call API without load parameters to establish baseline performance
- **Resource-Specific Testing**: Enable individual load types to identify bottlenecks
- **Stress Testing**: Enable all load types to test system limits
- **Scalability Testing**: Gradually increase concurrent requests with various load combinations

### Monitoring Validation
- **APM Testing**: Validate New Relic monitoring accuracy under different load conditions
- **Alert Testing**: Trigger specific resource usage patterns to test monitoring alerts
- **Dashboard Validation**: Generate predictable load patterns for dashboard verification

### Infrastructure Testing
- **Database Performance**: Test RDS PostgreSQL performance under various read/write loads
- **EC2 Capacity Planning**: Understand t3.micro instance limits under different workloads
- **Network Testing**: Validate application behavior under simulated network delays

## License

MIT