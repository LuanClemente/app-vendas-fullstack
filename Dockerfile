# Etapa 1 - Build (Maven + JDK 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY app_vendas/app-vendas/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

COPY app_vendas/app-vendas ./
RUN mvn clean package -DskipTests

# Etapa 2 - Runtime (JDK 21)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
