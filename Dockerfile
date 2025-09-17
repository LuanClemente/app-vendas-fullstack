# Usa uma imagem com Maven e JDK para build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY app_vendas/app-vendas /app

RUN mvn clean package -DskipTests

# Usa apenas JDK para rodar
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/app-vendas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
