# Etapa 1: compila o projeto (Java 25, igual ao pom.xml)
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# Etapa 2: imagem final só com o Java de execução
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 app && mkdir -p /app/uploads && chown app /app/uploads
COPY --from=build /app/target/*.jar app.jar
USER app
ENV UPLOADS_DIR=/app/uploads
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
