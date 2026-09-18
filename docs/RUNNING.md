# Setup and Testing Guide

This guide provides step-by-step instructions for building, running, and testing the project (Backend and Frontend).

---

## 1. Primary Method: Using Docker (Recommended)

If Docker Desktop is installed and running, local installations of Java, Maven, or Node.js are not required. Docker will automatically isolate and prepare the environment.

### Running the Application

Open a terminal in the project root directory (where the `docker-compose.yml` file is located) and execute:

```bash
docker compose up --build -d
```
*(The `-d` flag runs the containers in detached mode, running them in the background).*

Once the build process completes:
* **Frontend Application**: Accessible at [http://localhost:5173](http://localhost:5173)
* **Backend API**: Running implicitly at `http://localhost:8080`

To stop the services, execute:
```bash
docker compose down
```

### Running Tests and Viewing Coverage Reports

The project includes an automated testing suite and security checks. These can be executed entirely through Docker.

**A) Fast Test Execution (Without visual reports):**
Ideal for quickly verifying that the code is passing all tests.
```bash
docker build --target test ./backend
```

**B) Full Test Execution with HTML Reports (Coverage and Security):**
Execute the following command. This will download a Maven image, mount the source code, run the tests, and save the resulting `.html` files directly to the local `backend/target/` directory.

* **PowerShell (Windows):**
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.9-eclipse-temurin-25-alpine mvn clean test jacoco:report dependency-check:check
  ```
* **Bash (Linux/macOS):**
  ```bash
  docker run --rm -v "$(pwd)/backend:/app" -w /app maven:3.9-eclipse-temurin-25-alpine mvn clean test jacoco:report dependency-check:check
  ```

Once the process finishes, open these files in a web browser to view the graphical results:
* **Code Coverage Report (JaCoCo):** Open `backend/target/site/jacoco/index.html`
* **Vulnerability Audit (OWASP):** Open `backend/target/dependency-check-report.html`

---

## 2. Manual Method: Local Development Environments

For development purposes where Docker is not preferred, the following dependencies must be installed locally:
- **Java 25** (e.g., Eclipse Temurin 25)
- **Apache Maven 3.9+** (Added to the system PATH)
- **Node.js 20+** and **npm**

### Running the Application Locally

1. **Backend (Spring Boot):**
   Open a terminal, navigate to the backend directory, and start the service:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   *(The backend process will block the terminal).*

2. **Frontend (Vite / React):**
   Open a **new** terminal (to avoid interrupting the backend), navigate to the frontend directory, install dependencies, and start the development server:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   *(The application will be available at `http://localhost:5173`, and any source code changes will reflect instantly).*

### Executing Tests Locally (With Maven)

From a terminal located in the `backend/` directory:

* **Run all tests and generate the JaCoCo coverage report:**
  ```bash
  mvn clean test jacoco:report
  ```
* **Run API contract tests only:**
  ```bash
  mvn test -Dtest=ApiContractTest
  ```
* **Run vulnerability audit (OWASP Dependency-Check):**
  ```bash
  mvn dependency-check:check
  ```
*(Similarly to the Docker approach, the `.html` reports will be generated in the `backend/target/` directory).*

---

## 3. Environment Variables

If connection configurations need to be modified (e.g., for cloud deployment), the following variables are available:

| Variable | Target | Local Default Value | Description |
| :--- | :--- | :--- | :--- |
| `VITE_API_URL` | Frontend | `http://localhost:8080/api/v1/game` | The full URL where the frontend sends REST requests. |
| `CORS_ALLOWED_ORIGINS` | Backend | `*` or `http://localhost:5173` | Allowed web origins to prevent browser CORS blocks. |
| `PORT` | Backend | `8080` | The port where the Spring Boot server listens. |

*(Note: When running locally with `docker-compose.yml`, the default values are automatically injected and require no additional configuration).*
