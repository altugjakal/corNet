FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar
RUN mkdir -p src/files

EXPOSE 7777
ENTRYPOINT ["java", "-jar", "app.jar"]
