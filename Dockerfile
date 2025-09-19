# ======================
# Etapa 1 - Build (Maven + JDK 21)
# ======================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copia o pom.xml primeiro para cache de dependências
COPY app_vendas/app-vendas/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copia o código da aplicação
COPY app_vendas/app-vendas ./ 

# Compila o projeto sem rodar os testes
RUN mvn clean package -DskipTests

# ======================
# Etapa 2 - Runtime (JDK 21)
# ======================
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/app-vendas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

