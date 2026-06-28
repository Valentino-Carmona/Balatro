# Etapa 1: Compilación (Build)
FROM maven:3.8-eclipse-temurin-11-alpine AS build
WORKDIR /app

# Copiamos el pom y descargamos dependencias offline para cachear capas
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente y compilamos sin ejecutar tests para acelerar el build
COPY src ./src
COPY data ./data
RUN mvn clean package -DskipTests

# Etapa 2: Entorno de ejecución (Runtime)
FROM eclipse-temurin:11-jre-alpine

# Por seguridad, creamos un usuario no root
RUN addgroup -g 1001 -S appgroup && adduser -S appuser -u 1001 -G appgroup
WORKDIR /app

# Solo copiamos el ejecutable desde la fase de build
COPY --from=build --chown=appuser:appgroup /app/target/balatro-engine-0.0.1.jar ./app.jar

# Bajamos privilegios
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
