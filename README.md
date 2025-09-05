# Spring Boot Load Testing Application

This is a comprehensive Spring Boot application designed for performance monitoring and load testing with New Relic integration. The application provides configurable load generation across multiple system resources with optimized database operations and automatic cleanup functionality.

## Requirements

### Prerequisites

#### Development Environment
- **Java 17** or higher
- **Maven 3.8+** for dependency management and building
- **Git** for version control

#### Database
- **PostgreSQL** database instance (local or cloud-based like AWS RDS)
- Database should be accessible from your deployment environment
- Required database permissions: CREATE, SELECT, INSERT, UPDATE, DELETE

#### Cloud Infrastructure (for deployment)
- **AWS EC2** instance running Ubuntu (recommended: t3.medium or larger)
- **EC2 Security Groups** configured to allow:
  - SSH (port 22) for deployment
  - HTTP (port 8080) for application access
  - PostgreSQL (port 5432) for database connectivity
- **SSH Key Pair** for EC2 access

#### Monitoring (Optional)
- **New Relic** account and license key for APM monitoring
- **Prometheus** endpoint exposed for metrics collection

### File and Folder Structure

#### On Development Machine
```
project-root/
├── .github/
│   └── workflows/
│       └── deploy.yml          # GitHub Actions deployment workflow
├── src/
│   └── main/
│       ├── java/               # Java source code
│       └── resources/
│           └── application.properties  # Configuration file
├── pom.xml                     # Maven dependencies and build config
├── README.md                   # This file
├── .gitignore                  # Git ignore rules
└── .gitattributes              # Git attributes
```

#### On EC2 Server
```
/home/ubuntu/
├── springboot-app/             # Application deployment directory
│   ├── *.jar                   # Deployed JAR file
│   └── logs/                   # Application logs directory
│       └── application.log     # Main application log file
├── newrelic/                   # New Relic agent directory (if using New Relic)
│   ├── newrelic.jar           # New Relic Java agent
│   └── newrelic.yml           # New Relic configuration
└── .ssh/                       # SSH keys directory
    └── authorized_keys         # SSH public keys for access
```

### Environment Variables

#### GitHub Repository Secrets
Configure these secrets in your GitHub repository (Settings → Secrets and variables → Actions):

| Secret Name | Description | Example Value |
|-------------|-------------|---------------|
| `EC2_HOST` | EC2 instance public IP or DNS | `ec2-xx-xx-xx-xx.compute-1.amazonaws.com` |
| `EC2_USER` | EC2 SSH username | `ubuntu` |
| `EC2_SSH_KEY` | Private SSH key for EC2 access | `-----BEGIN OPENSSH PRIVATE KEY-----...` |
| `DATABASE_URL` | PostgreSQL JDBC connection string | `jdbc:postgresql://your-db-host:5432/database_name` |
| `DATABASE_USERNAME` | Database username | `your_db_user` |
| `DATABASE_PASSWORD` | Database password | `your_secure_password` |

#### Setting Up GitHub Secrets
1. Go to your GitHub repository
2. Click on **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret with its corresponding value

#### Environment Variables on EC2 Server
The application reads database configuration from environment variables. These can be set in multiple ways:

##### Option 1: System Environment Variables
```bash
export DATABASE_URL="jdbc:postgresql://your-db-host:5432/database_name"
export DATABASE_USERNAME="your_db_user"
export DATABASE_PASSWORD="your_secure_password"
```

##### Option 2: Systemd Service Configuration
If using systemd service, add environment variables to the service file:
```ini
[Unit]
Description=Spring Boot Application
After=network.target

[Service]
Type=forking
User=ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/springboot-app/application.jar
Environment=DATABASE_URL=jdbc:postgresql://your-db-host:5432/database_name
Environment=DATABASE_USERNAME=your_db_user
Environment=DATABASE_PASSWORD=your_secure_password
Restart=always

[Install]
WantedBy=multi-user.target
```

### EC2 Server Setup Requirements

#### System Requirements
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Verify Java installation
java -version

# Create application directory
sudo mkdir -p /home/ubuntu/springboot-app
sudo mkdir -p /home/ubuntu/springboot-app/logs
sudo chown -R ubuntu:ubuntu /home/ubuntu/springboot-app
```

#### Systemd Service Setup
Create a systemd service file for automatic startup and management:

```bash
sudo nano /etc/systemd/system/springboot-app.service
```

Add the following content:
```ini
[Unit]
Description=Spring Boot Load Testing Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/springboot-app
ExecStart=/usr/bin/java -jar /home/ubuntu/springboot-app/springboot-app-1.0.jar
Environment=DATABASE_URL=jdbc:postgresql://your-db-host:5432/database_name
Environment=DATABASE_USERNAME=your_db_user
Environment=DATABASE_PASSWORD=your_secure_password
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start the service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable springboot-app
sudo systemctl start springboot-app
```

#### Network Configuration
Ensure your EC2 security group allows:
- **Inbound Rules:**
  - SSH (port 22) from your IP
  - HTTP (port 8080) from desired sources
  - HTTPS (port 443) if using SSL termination
- **Outbound Rules:**
  - All traffic (for database and external service connections)

### Database Setup

#### PostgreSQL Database Requirements
1. **Database Creation:**
   ```sql
   CREATE DATABASE your_database_name;
   CREATE USER your_db_user WITH ENCRYPTED PASSWORD 'your_secure_password';
   GRANT ALL PRIVILEGES ON DATABASE your_database_name TO your_db_user;
   ```

2. **Table Creation:**
   The application will automatically create the required `ip_log` table using JPA/Hibernate DDL auto-update.

3. **Network Access:**
   - Ensure your database allows connections from your EC2 instance IP
   - Configure database security groups appropriately

### Optional Integrations

#### New Relic APM Setup
1. **Download New Relic Agent:**
   ```bash
   cd /home/ubuntu
   mkdir newrelic
   cd newrelic
   wget https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip
   unzip newrelic-java.zip
   ```

2. **Configure New Relic:**
   Edit `/home/ubuntu/newrelic/newrelic.yml` with your license key and app name.

3. **Update Systemd Service:**
   Modify the ExecStart line in your systemd service:
   ```ini
   ExecStart=/usr/bin/java -javaagent:/home/ubuntu/newrelic/newrelic.jar -jar /home/ubuntu/springboot-app/springboot-app-1.0.jar
   ```

#### Prometheus Metrics
The application exposes Prometheus metrics at `/actuator/prometheus`. Configure your Prometheus server to scrape:
```yaml
- job_name: 'spring-boot-app'
  static_configs:
    - targets: ['your-ec2-ip:8080']
  metrics_path: '/actuator/prometheus'
```

### Deployment Process

#### Automatic Deployment (GitHub Actions)
1. Push code changes to the `main` branch
2. GitHub Actions will automatically:
   - Build the application with Maven
   - Copy the JAR file to EC2
   - Deploy and restart the service

#### Manual Deployment
```bash
# Build locally
mvn clean package -DskipTests

# Copy to EC2
scp -i your-key.pem target/*.jar ubuntu@your-ec2-ip:/home/ubuntu/springboot-app/

# SSH to EC2 and restart service
ssh -i your-key.pem ubuntu@your-ec2-ip
sudo systemctl restart springboot-app
```

### Verification Steps

#### After Deployment
1. **Check Service Status:**
   ```bash
   sudo systemctl status springboot-app
   ```

2. **View Application Logs:**
   ```bash
   tail -f /home/ubuntu/springboot-app/logs/application.log
   ```

3. **Test Application:**
   ```bash
   curl -X POST "http://localhost:8080/greet?name=TestUser"
   ```

4. **Check Health Endpoint:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

5. **Verify Prometheus Metrics:**
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

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

### Database Management
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

# Enable database cleanup
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

## Configuration

All load testing parameters can be configured in `application.properties`. See the configuration file for detailed parameter descriptions and default values.

## Troubleshooting

### Common Issues
1. **Database Connection Issues**: Verify environment variables and network connectivity
2. **Service Won't Start**: Check systemd logs with `sudo journalctl -u springboot-app -f`
3. **Application Not Responding**: Verify port 8080 is open and service is running
4. **Deployment Failures**: Check GitHub Actions logs and SSH connectivity

### Useful Commands
```bash
# Service management
sudo systemctl status springboot-app     # Check status
sudo systemctl restart springboot-app    # Restart service
sudo systemctl stop springboot-app       # Stop service

# Log monitoring
sudo journalctl -u springboot-app -f     # Follow systemd logs
tail -f /home/ubuntu/springboot-app/logs/application.log  # Follow application logs

# Process monitoring
ps aux | grep java                       # Check Java processes
netstat -tlnp | grep 8080              # Check port usage
```