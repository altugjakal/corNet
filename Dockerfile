
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar
RUN mkdir -p src/files

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]