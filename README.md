# Balatro Web MVP (Motor Autoritativo Stateless)

Este proyecto es la implementación web (MVP) del clásico juego de cartas, dividido arquitectónicamente en un backend robusto de Spring Boot que expone APIs REST y un frontend independiente con estética "Rubber Hose".

## Estructura del Repositorio

- `/backend`: Contiene el código fuente, dependencias y datos del **Backend** (Java 11, Maven, Spring Boot).
- `/frontend`: Contiene el código del **Frontend** (React, TypeScript, Vite, Tailwind CSS).

Ambas capas operan bajo el Principio de Responsabilidad Única. El frontend es simplemente una marioneta visual que no calcula puntajes, delegando esa lógica de forma autoritativa al backend.

---

## 🚀 Cómo ejecutar con Docker (Recomendado)

La forma más rápida y profesional de levantar el proyecto sin instalar lenguajes, entornos ni compiladores locales es mediante **Docker**. Todo el proyecto (tanto el Backend como el Frontend) correrán de forma aislada y orquestada en contenedores.

> **¡ATENCIÓN!** 
> Si recibís un error del tipo `failed to connect to the docker API` o `El sistema no puede encontrar el archivo especificado`, significa que el motor de Docker está apagado en tu máquina. **Debés abrir la aplicación "Docker Desktop" en Windows/Mac** y esperar a que el motor arranque antes de ejecutar comandos.

Asegurate de tener encendido [Docker Desktop](https://www.docker.com/products/docker-desktop/) y ejecutá el siguiente comando desde la raíz del proyecto:

```bash
docker-compose up --build -d
```
*(El flag `-d` lo correrá en segundo plano).*

Una vez que termine de compilar (puede tomar unos minutos la primera vez):
- **Jugar (Frontend)**: Abre tu navegador en [http://localhost:5173](http://localhost:5173).
- **Backend API**: Estará escuchando silenciosamente en `http://localhost:8080`.

Para detener los servicios:
```bash
docker-compose down
```

### 🧪 Ejecutar Tests y Ver Cobertura dentro de Docker (Sin instalar Maven)

El proyecto cuenta con dos tipos de tests automatizados del backend:

| Tipo | Descripción | Herramienta |
|------|-------------|-------------|
| **Unit Tests** | Prueban clases de forma aislada con mocks | `@WebMvcTest` + Mockito |
| **Integration Tests** | Prueban flujos HTTP end-to-end con servidor real | `@SpringBootTest` + TestRestTemplate |
| **Contract Tests** | Verifican estructura, tipos y nombres de campos del JSON | `@SpringBootTest` + Jackson JsonNode |

**Correr todos los tests (unitarios + integración + contrato) y generar cobertura:**
```powershell
docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn clean test jacoco:report
```

**Correr solo los tests de integración:**
```powershell
docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn test "-Dtest=GameFlowIntegrationTest" jacoco:report
```

**Correr solo los tests de contrato de API:**
```powershell
docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn test "-Dtest=ApiContractTest" jacoco:report
```

*(Esto descargará temporalmente Maven, mapeará tu carpeta local de código, correrá los tests, generará el reporte de cobertura y luego se auto-destruirá).*

Para ver el reporte de cobertura generado, abrí el siguiente archivo en tu navegador web:
`backend/target/site/jacoco/index.html`

---

## 🛠 Comandos Manuales para Desarrolladores

Si sos un desarrollador, querés editar el código y preferís no usar Docker temporalmente, podés levantar los entornos por tu cuenta. Es necesario que tengas instalados **Java 11**, **Maven** y **Node.js** en tu sistema local.

### 1. Ejecutar Pruebas y Obtener Cobertura (Backend)
Para asegurarte de que la lógica de dominio y los cálculos de las reglas de poker funcionan correctamente, y generar el reporte de cobertura (JaCoCo):
```bash
cd backend
mvn clean test jacoco:report
```
*(Este comando compila el proyecto, corre todos los tests de JUnit, valida la arquitectura con ArchUnit y genera un reporte de cobertura).*

Para ver el reporte de cobertura generado, abrí el siguiente archivo en tu navegador web:
`backend/target/site/jacoco/index.html`

### 2. Levantar el Servidor Backend (API REST)
El servidor alojará la lógica autoritativa del juego en el puerto `8080`. Se debe ejecutar desde la carpeta del backend (donde está el `pom.xml`):
```bash
cd backend
mvn spring-boot:run
```
*(El servidor quedará encendido escuchando peticiones REST en `http://localhost:8080/api/v1/...`).*

### 3. Levantar la Interfaz Visual (Frontend)
Abre **una nueva terminal** para no interrumpir el backend, ingresa a la subcarpeta del frontend y ejecuta los comandos de Node:

**A. Instalar dependencias (Solo la primera vez que clonas el repositorio):**
```bash
cd frontend
npm install
```

**B. Levantar el entorno de desarrollo:**
```bash
cd frontend
npm run dev
```
*(Vite te proporcionará un enlace, generalmente `http://localhost:5173`, que puedes abrir en tu navegador para interactuar con la UI).*

---

## 🛠 Comandos Extra para Producción

Si deseas compilar el código para prepararlo hacia un despliegue en la nube (ej. AWS, Heroku, Vercel, VPS):

**Generar el ejecutable `.jar` del Backend:**
```bash
cd backend
mvn clean package
```
*(Esto creará un `.jar` en la carpeta `/backend/target/` que puedes ejecutar en cualquier servidor con `java -jar nombre-del-archivo.jar`).*

**Compilar el Frontend estático:**
```bash
cd frontend
npm run build
```
*(Generará una carpeta `/frontend/dist/` lista para ser servida en cualquier CDN o servicio de alojamiento estático).*
