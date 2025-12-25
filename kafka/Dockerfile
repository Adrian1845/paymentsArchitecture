# 1. Base Image: Kafka needs Java to run.
FROM eclipse-temurin:21-jre-alpine

# 2. Set environment variables for Kafka version and location
ENV KAFKA_VERSION=3.7.0
ENV SCALA_VERSION=2.13
ENV KAFKA_HOME=/opt/kafka

# 3. Install dependencies (bash and curl are needed for the download and startup)
RUN apk add --no-cache bash curl

# 4. Download and Extract Kafka binaries from Apache
RUN curl -O https://archive.apache.org/dist/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz && \
    mkdir -p ${KAFKA_HOME} && \
    tar -xzf kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz -C ${KAFKA_HOME} --strip-components=1 && \
    rm kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz

# 5. Set the working directory
WORKDIR ${KAFKA_HOME}

# 6. Expose the Kafka port
EXPOSE 9092

# 7. Start Kafka
# We use a shell command to ensure it points to the internal Zookeeper
# We define two listeners:
# 1. INTERNAL: Used by Kafka UI and Java App (port 29092)
# 2. EXTERNAL: Used by your Windows PC (port 9092)

CMD ["bin/kafka-server-start.sh", "config/server.properties", \
     "--override", "zookeeper.connect=zookeeper:2181", \
     "--override", "listeners=INTERNAL://0.0.0.0:29092,EXTERNAL://0.0.0.0:9092", \
     "--override", "advertised.listeners=INTERNAL://kafka_custom:29092,EXTERNAL://localhost:9092", \
     "--override", "listener.security.protocol.map=INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT", \
     "--override", "inter.broker.listener.name=INTERNAL"]