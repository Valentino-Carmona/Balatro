# Setup, Execution, and Testing Guide

This guide provides detailed, step-by-step instructions for building, running, and testing the Balatro Web application, both using containerized environments (Docker) and local development setups.

---

## 1. Prerequisites

You can run and test this project through two different workflows:

### Option A: Containerized with Docker (Recommended)
* **Docker Desktop** installed and running (Windows / macOS / Linux).
* No local installation of Java, Maven, or Node.js is required.

### Option B: Native Local Development
* **JDK 11** (e.g., Eclipse Temurin 11).
* **Apache Maven 3.8+** (must be configured in your system `PATH`).
* **Node.js 20+** and **npm**.

---

## 2. Running the Application

> [!IMPORTANT]
> Always execute Docker commands from the **root directory** of the repository (`Balatro/`), where `docker-compose.yml` is located.

### 2.1. Using Docker Compose (Full Stack)

To build and start both the backend API and frontend client in isolated containers:

```bash
# From the repository root:
docker compose up --build
```

To run in detached mode (background):
```bash
docker compose up --build -d
```

To stop all running services:
```bash
docker compose down
```

Once started:
* **Frontend Web UI**: [http://localhost:5173](http://localhost:5173)
* **Backend REST API**: [http://localhost:8080](http://localhost:8080) (Health / endpoints under `/api/v1/game/*`)

---

### 2.2. Running Manually in Local Environments

If you prefer to run services natively without Docker:

#### Step 1: Start the Backend (Spring Boot)
Open a terminal:
```bash
cd backend
mvn spring-boot:run
```
The API will start listening on port `8080`.

#### Step 2: Start the Frontend (Vite / React)
Open a second terminal:
```bash
cd frontend
npm install
npm run dev
```
The client will start and provide a local URL (typically `http://localhost:5173`).

---

## 3. Running Tests and Quality Checks

### 3.1. Running Tests via Docker (No Local Maven Required)

If your local environment does not have Maven installed in `PATH` (`mvn: command not found`), you can run all automated tests and quality tools inside an official Maven container:

#### Run All Tests and Generate JaCoCo Coverage Report
* **PowerShell (Windows)**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn clean test jacoco:report
  ```
* **Bash (Linux / macOS)**:
  ```bash
  docker run --rm -v "$(pwd)/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn clean test jacoco:report
  ```

#### Run Only Integration Tests
* **PowerShell**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn test "-Dtest=GameFlowIntegrationTest" jacoco:report
  ```

#### Run Only API Contract Tests
* **PowerShell**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn test "-Dtest=ApiContractTest" jacoco:report
  ```

#### Run Security Vulnerability Audit (OWASP Dependency-Check)
* **PowerShell**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn dependency-check:check
  ```

---

### 3.2. Running Tests with Local Maven

If you have Maven installed locally:

#### Run All Unit and Integration Tests with JaCoCo:
```bash
cd backend
mvn clean test jacoco:report
```

#### Run API Contract Tests:
```bash
cd backend
mvn test -Dtest=ApiContractTest
```

#### Run OWASP Dependency-Check:
```bash
cd backend
mvn dependency-check:check
```

---

## 4. Inspecting Reports

After executing the test and audit commands, reports are generated in the `backend/target/` directory:

* **JaCoCo Coverage Report**:
  Open in your web browser:
  `backend/target/site/jacoco/index.html`
* **OWASP Vulnerability Report**:
  Open in your web browser:
  `backend/target/dependency-check-report/dependency-check-report.html`

---

## 5. Environment Variables

| Variable | Target | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `VITE_API_URL` | Frontend | `http://localhost:8080/api/v1/game` | Target API base URL consumed by the client. |
| `CORS_ALLOWED_ORIGINS` | Backend | `http://localhost:5173` | Comma-separated allowed origins for CORS headers. |

* A sample file is available at `backend/.env.example`.
* Docker Compose automatically supplies functional defaults if no `.env` file is present.

---

## 6. Troubleshooting

### 1. `mvn: The term 'mvn' is not recognized`
* **Cause**: Apache Maven is not installed locally or its `bin` directory is not registered in your operating system's `PATH` environment variable.
* **Resolution**:
  * Either execute test commands using the **Docker containerized commands** provided in [Section 3.1](#31-running-tests-via-docker-no-local-maven-required).
  * Or install Apache Maven 3.8+ and add it to your system PATH, or run the project through an IDE (such as IntelliJ IDEA or VS Code with Extension Pack for Java).

### 2. `env file ... not found` when running Docker Compose
* **Cause**: Running `docker compose` from inside the `backend/` directory instead of the project root.
* **Resolution**: Navigate back to the repository root directory before running Compose:
  ```bash
  cd f:\ALL\PROGRAMACION\Proyectos\Balatro\Balatro\Balatro
  docker compose up --build
  ```

### 3. Docker connection error / daemon not running
* **Cause**: Docker Desktop is not running or has not finished initializing.
* **Resolution**: Start Docker Desktop and wait until the whale icon indicates "Engine running" before executing docker commands.
