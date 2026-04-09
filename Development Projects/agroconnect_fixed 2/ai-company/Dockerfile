FROM eclipse-temurin:21-jdk AS builder

RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/target/ai-company-1.0.0.jar ./app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]
