# paymentsArchitecture
🏗️ Core Project Stack
- Database (PostgreSQL): Persistent relational storage for managing payments and user data.

- Caching (Redis): High-speed caching layer implemented to optimize frequent queries and reduce DB overhead.

- Messaging (Kafka & Zookeeper): Asynchronous event-driven strategy for message processing, ensuring the system is decoupled and scalable.

📧 Email Strategy

- Notification Service: Integrated with Mailtrap to capture and preview welcome emails and transaction notifications in a secure Sandbox environment.

- Kafka Failed Event Service: Send an email when an error reaches status CRITICAL FAILURE

<img width="1014" height="715" alt="imagen" src="https://github.com/user-attachments/assets/fbce11ae-348a-4276-b0f0-6d86f3357472" />


📊 Full-Stack Observability

We use Prometheus and Grafana to provide 360° visibility:

- Application Metrics: JVM health and Spring Boot performance tracking via Actuator.
- Infrastructure Metrics: Dedicated exporters for Kafka, Redis, and PostgreSQL, allowing for real-time bottleneck detection.

🛡️ Security & Access Control

- JWT (JSON Web Tokens): Stateless security implementation for robust user authentication.

- Security Filters: Token processing via custom filters integrated into the Spring Security context.

- Custom Exception Handling: Global error control structure (@ControllerAdvice) to standardize system error responses, improving both client experience and debugging.

🏗️ Code Quality & DevOps

- SonarQube: Static code analysis to detect code smells and vulnerabilities, ensuring high-quality standards and test coverage.

- JaCoCo: To ensure a minimum code coverage 

- CI Pipeline: Automated Continuous Integration to validate every code change before it reaches the main branch.
    
To ensure connectivity, services are orchestrated across three Docker networks
