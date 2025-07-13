FROM maven:3.9.10-amazoncorretto-24 AS builder

WORKDIR /app

COPY openapi.yaml ../openapi.yaml

COPY backend/pom.xml .
COPY backend/src ./src

RUN mvn clean package

FROM amazoncorretto:24-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
