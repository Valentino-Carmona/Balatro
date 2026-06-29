# Balatro Web MVP (Motor Autoritativo Stateless)

[![CI/CD Pipeline](https://github.com/Valentino-Carmona/Balatro/actions/workflows/ci.yml/badge.svg)](https://github.com/Valentino-Carmona/Balatro/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/Valentino-Carmona/Balatro/graph/badge.svg)](https://codecov.io/gh/Valentino-Carmona/Balatro)

> [!NOTE]
> **Estado del Proyecto:** Esta versión en Java constituye un **MVP (Producto Mínimo Viable)** presentado al cliente para validar la experiencia del clásico juego de cartas Balatro. Representa el juego prácticamente terminado en cuanto a su lógica y mecánicas principales, desarrollo que sirvió como base para ser portado posteriormente y funcionar en la página web del cliente.

¡Bienvenido a **Balatro Web MVP 2**! Este proyecto es una implementación moderna y altamente interactiva del exitoso roguelike de póker, diseñado con una arquitectura robusta orientada a servicios (Backend en Spring Boot) y una estética "Rubber Hose" sumamente pulida para ofrecer una experiencia inmersiva.

## 🎮 ¿De qué se trata el juego?

**Balatro** es un roguelike de construcción de mazos inspirado en el póker. 

El jugador debe superar rondas (ciegas y antes) logrando un puntaje objetivo (`targetScore`) mediante el juego de manos de póker tradicionales. La clave del éxito no radica solo en las cartas obtenidas, sino en la combinación estratégica de distintos potenciadores.

### Reglas Clave (Mecánica de Gameplay):
* **Manos de Póker**: Se juegan combinaciones clásicas (Par, Doble Par, Color, Full House, etc.) para sumar fichas y multiplicadores base.
* **Comodines (Jokers)**: Alteran pasivamente las reglas del juego, otorgando multiplicadores exponenciales, fichas extra u otros beneficios condicionales (ej: estrategias de aumento de multiplicador, puntos extra, o ventajas al descartar cartas).
* **Gestión de Recursos**: El jugador cuenta con una cantidad limitada de manos jugables y descartes por ronda. Superar una ronda exitosamente otorga dinero para usar en la tienda.

---

## ✨ Características Especiales y Jugabilidad

Este motor de Balatro ha sido desarrollado para brindar una experiencia web competitiva, segura y muy disfrutable:

### 🧠 Motor Autoritativo Stateless
A diferencia de juegos puramente del lado del cliente, aquí el frontend es una "marioneta visual". Toda la lógica de cálculo de puntajes, resolución de manos válidas, aplicación de estrategias de jokers y estados del jugador reside y se valida exclusivamente en el servidor Backend. 

### 🛡️ Auditoría y Seguridad
El motor fue fortificado con prácticas de seguridad de estándar industrial, incluyendo mitigaciones contra vulnerabilidades comunes. Implementa cabeceras HTTP defensivas (contra Clickjacking, XSS), un sistema robusto de control de dependencias mediante SCA y manejo seguro de sesiones en memoria sin exposición a vectores de ataque de Path Traversal.

### 📐 API REST Robusta y Resiliente
Comunicación fluida mediante endpoints JSON validados estrictamente. Se incorporaron tests de contratos, filtros CORS configurados minuciosamente y tolerancia a fallos ante cuerpos de petición malformados, retornando respuestas HTTP limpias y coherentes.

---

## 🕹️ Interacción y Controles

La experiencia se juega íntegramente a través de la interfaz visual web, de forma intuitiva:

| Acción | Descripción |
| :--- | :--- |
| **Clic Izquierdo** | Selecciona o deselecciona cartas en tu mano para preparar una jugada o descarte. |
| **Botón "Jugar Mano"** | Envía las cartas seleccionadas al motor para su evaluación y cálculo de puntaje. |
| **Botón "Descartar"** | Intercambia las cartas seleccionadas por nuevas del mazo, consumiendo un descarte de la ronda. |
| **Interacción con la Tienda** | Entre rondas, haz clic en potenciadores o jokers para comprarlos utilizando tu balance de dinero actual. |

---

## 🎨 Aspecto Visual

El proyecto destaca por una profunda inmersión estética inspirada en el estilo retro **"Rubber Hose"**, cuidando hasta el mínimo detalle de la experiencia de usuario:

* 🎲 **Fondo de Fieltro Animado**: La mesa y los menús lucen un gradiente dinámico (`.table-bg-animated`) que simula la iluminación cambiante sobre un tapete real de casino, de forma sutil y relajante.
* 🪙 **Fichas Flotantes Interactivas**: Elementos vectoriales retro orbitan de manera asíncrona en los márgenes de la pantalla, aportando vida y movimiento al menú.
* ✨ **Feedback Táctil Premium**: Al pasar el ratón por cartas, botones principales o elementos de la tienda, estos responden con una vibración de alta frecuencia y un aumento de brillo, entregando una respuesta sumamente enérgica y satisfactoria (Hover Vibration).
* 🃏 **Disposición Intuitiva**: Cartas distribuidas en abanico y un panel claro de estadísticas que refleja instantáneamente el estado dictaminado por el servidor.

---

## 🏗️ Arquitectura y Estructura del Proyecto

El proyecto está diseñado bajo un modelo de arquitectura **Cliente-Servidor** siguiendo rígidamente el principio de separación de responsabilidades (*Separation of Concerns*). 

### 1. Backend (Java 11 / Spring Boot)
Toda la lógica pura de negocio y dominio reside en el directorio `/backend`.
* **Capa de Modelo**: Estructuras fundamentales (Cartas, Jokers, Rondas, Cálculo de Puntajes con Patrón Strategy) libres de acoplamientos del framework.
* **Capa de Servicios**: Orquesta los flujos (creación de partidas, transiciones de rondas y tiendas).
* **Capa de Controladores**: Expone la API REST manejando peticiones (como `/api/v1/game/play`).
* **Seguridad y Auditoría**: Configuración explícita de `spring-boot-starter-security` con reglas de cabeceras seguras.

### 2. Frontend Web (React / TypeScript / Vite / Tailwind CSS)
La capa de presentación ha sido aislada en `/frontend`.
* Renderiza la UI y gestiona efectos visuales, ignorando por completo la matemática y lógica del juego de cartas subyacente.
* Todo se comunica empaquetando eventos a la API, esperando los resultados para animar de vuelta la respuesta en la pantalla.

### 🛡️ Buenas Prácticas de Ingeniería Implementadas
* **Alta Fiabilidad vía Testing**: El backend supera un 80% de cobertura de código validado con `JaCoCo`. Cuenta con Unit Tests aislados (Mockito), Integration Tests completos (end-to-end) y Contract Tests para verificar robustez de la API.
* **Desacoplamiento Extremo**: La independencia del Frontend/Backend fomenta mantenibilidad.
* **Dockerización Fullstack**: Todo el ecosistema (cliente y API) se levanta usando contenedores de Docker, asegurando paridad entre ambientes y evitando el problema "en mi máquina funciona".

---

## 🚀 Instalación y Ejecución

Guía paso a paso para clonar, instalar y correr el proyecto, además de ejecutar sus pruebas y ver el reporte de cobertura.

### 📥 1. Descargar el Proyecto

Si aún no tienes el código en tu máquina, primero descárgalo:

1. **Clona el repositorio** desde GitHub:
   ```bash
   git clone https://github.com/Valentino-Carmona/Balatro.git
   ```
2. **Navega al directorio del juego**:
   ```bash
   cd Balatro
   ```

---

### 🔐 2. Configuración de Variables de Entorno

El proyecto utiliza variables de entorno para comunicar el Frontend y el Backend de manera segura y dinámica, permitiendo que el código funcione tanto en local como en producción sin modificaciones.

- **Frontend (`VITE_API_URL`)**: Define la URL base a la cual el frontend enviará las peticiones HTTP. (Ej: `http://localhost:8080/api/v1`).
- **Backend (`CORS_ALLOWED_ORIGINS`)**: Define qué dominios o direcciones tienen permiso explícito para realizar peticiones a la API, previniendo accesos cruzados no autorizados. (Ej: `http://localhost:5173`).

> [!NOTE]
> Si las variables de entorno llegase a faltar localmente, el Backend está configurado con valores por defecto (ej. `server.port=${PORT:8080}`), por lo que arrancará de todos modos de manera segura. El Frontend, al carecer de la URL de la API, no podrá comunicarse hasta que se le provea la variable.

---

### 🎮 3. Instalación y Ejecución (Elige tu Opción)

Tienes dos alternativas para preparar y ejecutar el juego, dependiendo de tus herramientas.

#### 🐳 Opción A: Usando Docker (Recomendado)

Esta es la forma más rápida y profesional. No necesitas configurar entornos ni instalar Java/Node en tu máquina; Docker aislará todo por ti.

> **¡ATENCIÓN!** 
> Si recibes un error de conexión, significa que el motor de Docker está apagado en tu máquina. Abre la aplicación "Docker Desktop" y espera a que arranque antes de ejecutar el comando.

1. **Construye y levanta el contenedor** desde la consola:
   ```bash
   docker-compose up --build -d
   ```
   *(El flag `-d` lo correrá en segundo plano).*

Para detener los servicios, usa:
```bash
docker-compose down
```

#### 🛠️ Opción B: Usando Entornos Locales (Desarrolladores)

Si prefieres no usar Docker, puedes levantar los servicios manualmente. Necesitas Java 11, Maven y Node.js instalados.

1. **Levantar el Servidor Backend (API)**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
2. **Levantar la Interfaz Visual (Frontend)**:
   Abre una nueva terminal para no interrumpir el backend.
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

---

### 🌐 4. ¡A Jugar!

Una vez que el servidor reporte que está corriendo (por Docker o manual):

1. **Abre tu navegador web** e ingresa a la dirección del Frontend:
   [http://localhost:5173](http://localhost:5173)
2. *(El Backend operará de forma transparente escuchando en `http://localhost:8080`)*.
3. ¡Listo! Ya puedes disfrutar de Balatro Web MVP.

---

### 🧪 5. Correr las Pruebas y Observar Cobertura

El proyecto cuenta con un conjunto robusto de pruebas automatizadas y chequeo de cobertura.

* **Correr todos los tests (unitarios + integración + contrato) dentro de Docker**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn clean test jacoco:report
  ```

* **Correr solo los tests de integración**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn test "-Dtest=GameFlowIntegrationTest" jacoco:report
  ```

* **Correr solo los tests de contrato de API**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn test "-Dtest=ApiContractTest" jacoco:report
  ```

* **Correr auditoría de vulnerabilidades (OWASP Dependency Check) en Docker**:
  ```powershell
  docker run --rm -v "${PWD}/backend:/app" -w /app maven:3.8-eclipse-temurin-11-alpine mvn dependency-check:check
  ```

---  

* **Correr tests usando Maven Localmente**:
  ```bash
  cd backend
  mvn clean test jacoco:report
  ```

* **Correr auditoría de vulnerabilidades localmente**:
  ```bash
  cd backend
  mvn dependency-check:check
  ```

* **Para ver el reporte de cobertura generado**:
  Abre el siguiente archivo en tu navegador web:
  `backend/target/site/jacoco/index.html`

* **Para ver el reporte de seguridad generado**:
  Abre el siguiente archivo en tu navegador web:
`backend/target/dependency-check-report.html`