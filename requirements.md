# Requirements of HA 03: Concurrency
## Functions
- The entities of the system
  - Battery
  - EnergySource
  - EnergyConsumer
- Battery Management Service to control the charging and discharging
  - BatteryManagerService
- Testing related to the methods in BatteryManagerService
  - BatteryManagerServiceConcurrencyTest

## Dependencies

Format: `groupId:artifactId:version`

If it is without version, then it follows the corresponding version of Spring Boot.

- org.springframework.boot:spring-boot-starter-parent
- org.springframework.boot:spring-boot-starter
- org.springframework.boot:spring-boot-devtools
- org.springframework.boot:spring-boot-starter-test
- org.springframework.boot:spring-boot-configuration-processor
- org.projectlombok:lombok
- org.mockito:mockito-inline:5.2.0

## Dev Environment
- JDK 17