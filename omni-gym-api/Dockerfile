# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia pom.xml e faz download das dependências para cachear na camada do Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia os fontes e gera o build do JAR ignorando os testes
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o JAR compilado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Cria a pasta de upload de arquivos médicos na aplicação
RUN mkdir -p uploads

# Expõe a porta padrão do backend
EXPOSE 8080

# Comando para iniciar o servidor
ENTRYPOINT ["java", "-jar", "app.jar"]
