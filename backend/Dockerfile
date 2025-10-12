# -------------- build stage --------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

#Copy source and build
COPY src ./src
RUN mvn -q -DskipTests package

#------------------runtime stage ---------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the fat jaar ( rename if your artifact name differs)
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "jave $JAVA_OPTS -jar /app/app.jar"]