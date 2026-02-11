🏗️ Arquitectura de Core (Stack Principal)

- Base de Datos (PostgreSQL): Almacenamiento relacional persistente para la gestión de pagos y datos de usuario.

- Caché (Redis): Capa de caché de alta velocidad implementada para optimizar consultas frecuentes y reducir la carga de la base de datos.

- Mensajería (Kafka y Zookeeper): Estrategia asíncrona basada en eventos para el procesamiento de mensajes, garantizando que el sistema sea escalable y esté desacoplado.

📧 Estrategia de Correo Electrónico

- Servicio de Notificaciones: Integrado con Mailtrap para capturar y previsualizar correos de bienvenida y notificaciones de transacciones en un entorno de pruebas (Sandbox) seguro.

- Servicio de Eventos Fallidos en Kafka: Envío automático de correos electrónicos cuando un error alcanza el estado de FALLO CRÍTICO (CRITICAL FAILURE).

📊 Observabilidad Full-Stack

- Utilizamos Prometheus y Grafana para proporcionar una visibilidad de 360°:

- Métricas de Aplicación: Seguimiento del estado de la JVM y del rendimiento de Spring Boot mediante Actuator.

- Métricas de Infraestructura: Exportadores dedicados para Kafka, Redis y PostgreSQL, permitiendo la detección de cuellos de botella en tiempo real.

🛡️ Seguridad y Control de Acceso

- JWT (JSON Web Tokens): Implementación de seguridad sin estado (stateless) para una autenticación de usuarios robusta.

- Filtros de Seguridad: Procesamiento de tokens mediante filtros personalizados integrados en el contexto de Spring Security.

- Manejo de Excepciones Personalizado: Estructura de control de errores global (@ControllerAdvice) para estandarizar las respuestas de error del sistema, mejorando tanto la experiencia del cliente como la depuración (debugging).

🏗️ Calidad de Código y DevOps

- SonarQube: Análisis estático de código para detectar "code smells" y vulnerabilidades, asegurando altos estándares de calidad y cobertura de pruebas.

- JaCoCo: Implementado para garantizar un nivel mínimo de cobertura de código (code coverage).

- Pipeline de CI: Integración Continua automatizada para validar cada cambio de código antes de que llegue a la rama principal (main branch).

Configuración de Red: Para garantizar la conectividad, los servicios están orquestados a través de tres redes de Docker independientes.
