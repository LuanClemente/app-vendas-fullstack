# ======================
# Etapa 1 - Build (Maven + JDK)
# ======================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml primeiro para cache de dependências
COPY app_vendas/app-vendas/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copia o código da aplicação
COPY app_vendas/app-vendas ./ 

# Dá permissão ao mvnw (caso esteja no repo)
RUN chmod +x mvnw || true

# Compila o projeto sem rodar os testes
RUN mvn clean package -DskipTests

# ======================
# Etapa 2 - Runtime (somente JDK)
# ======================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o jar gerado do build
COPY --from=build /app/target/app-vendas-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta do Spring Boot
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
