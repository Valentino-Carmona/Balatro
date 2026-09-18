# Etapa 1: Descarga de dependencias (Capa cacheable)
FROM maven:3.9-eclipse-temurin-25-alpine AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Etapa 2: Pruebas (Test)
# Esta etapa aisla la ejecucion de los tests. Puedes correr solo esta etapa en CI/CD con: --target test
FROM deps AS test
COPY src ./src
COPY data ./data
RUN mvn test

# Etapa 3: Compilacion (Build)
# Reutilizamos las dependencias y compilamos. Si vienes de CI/CD, ya probaste, asi que skippeamos tests aqui.
FROM deps AS build
COPY src ./src
COPY data ./data
RUN mvn clean package -DskipTests

# Etapa 4: Entorno de ejecucion (Production Runtime)
FROM eclipse-temurin:25-jre-alpine AS production

# Mejores practicas: usuario no root por seguridad
RUN addgroup -g 1001 -S appgroup && adduser -S appuser -u 1001 -G appgroup
WORKDIR /app

# Variables de entorno con valores por defecto amigables para desarrollo local.
# En Render, estas se sobreescribiran inyectadas por la plataforma.
ENV PORT=8080
ENV CORS_ALLOWED_ORIGINS="*"

# Copiar artefacto compilado
COPY --from=build --chown=appuser:appgroup /app/target/balatro-engine-0.0.1.jar ./app.jar

# Bajar privilegios
USER appuser

# Exponer el puerto (Documentativo)
EXPOSE 8080

# Inyectamos el puerto de Render a las propiedades de Spring Boot dinamicamente.
# Tambien el entorno de Docker (CORS_ALLOWED_ORIGINS) sera mapeado a cors.allowed.origins por Spring Boot.
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
