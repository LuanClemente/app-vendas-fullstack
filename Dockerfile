# Etapa 1: Build da aplicação
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
WORKDIR /app/app_vendas/app-vendas
RUN mvn clean package -DskipTests

# Etapa 2: Executar a aplicação
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/app_vendas/app-vendas/target/app-vendas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
