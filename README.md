# Spring Boot IP Logger

This is a simple Spring Boot app that:

- Provides `POST /greet?name=...`
- Logs client IP, name, and timestamp to PostgreSQL
- Responds with formatted current time and last request info

## Setup

1. Configure PostgreSQL DB in `application.properties`
2. Run with `./mvnw spring-boot:run` or `mvn spring-boot:run`
3. Test via:
   ```
   curl -X POST "http://localhost:8080/greet?name=Jay"
   ```

## License

MIT