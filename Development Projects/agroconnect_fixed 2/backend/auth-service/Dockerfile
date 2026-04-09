FROM eclipse-temurin:21-jdk AS builder

RUN microdnf install -y maven || \
    (yum -y install maven && yum clean all) || \
    (apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*)

RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/target/auth-service-0.0.1.jar ./app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

