# ======================
# Etapa 1 - Build (Maven + JDK)
# ======================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro (para aproveitar cache do Maven)
COPY app_vendas/app-vendas/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Agora copia o restante do código
COPY app_vendas/app-vendas ./ 

# Compila o projeto sem rodar os testes
RUN mvn clean package -DskipTests

# ======================
# Etapa 2 - Runtime (somente JDK)
# ======================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o jar gerado da etapa anterior
COPY --from=build /app/target/app-vendas-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
