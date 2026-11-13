# Refrio Backend — ocho bounded contexts con DDD

**Versión 1.1.0: Java 21 + Spring Boot + Maven + PostgreSQL.**
Este proyecto replica el estilo académico del ejemplo *Learning Center* (agregados, objetos de valor, puertos de repositorio, adaptadores JPA y servicios de aplicación) y lo adapta a los ocho contextos reales del frontend Refrio.

Los mismos ocho contextos están presentes tanto en `src/main/java/com/refrio/platform/` como en `src/test/java/com/refrio/platform/`: `iam`, `inventory`, `operations`, `alerting`, `traceability`, `analytics`, `publicportal`, `events`. La carpeta `shared` es infraestructura transversal; `architecture` solo contiene tests y no es un noveno contexto.

**Pruebas y evidencias:** [docs/TEST_MATRIX.md](docs/TEST_MATRIX.md) · **Inventario:** [docs/MODULE_INVENTORY.md](docs/MODULE_INVENTORY.md) · **Arquitectura:** [docs/DDD_ARCHITECTURE.md](docs/DDD_ARCHITECTURE.md).

**Estado comprobado en este entorno:** 58 archivos de dominio compilados con Java 21 y 27 verificaciones sin dependencias externas superadas. La compilación total con Maven, las pruebas JUnit/HTTP y el despliegue Docker deben ejecutarse en un entorno con dependencias y contenedores disponibles; no se presentan como verificados.

---

## Actualización de alineación con la evaluación 1ASI0729 — v4

Se utilizó el PDF correcto del curso **Desarrollo de Aplicaciones Open Source (2026-20)**, que exige Java, Spring Boot, Spring Data JPA, Swagger/OpenAPI, Angular, i18n (inglés por defecto + español latinoamericano), DDD, entregas y evidencias reales.

- [Matriz de evaluación real y trabajo pendiente](docs/RUBRIC_ALIGNMENT_1ASI0729.md)
- [Design-level EventStorming: comandos, eventos, agregados y consultas](docs/EVENTSTORMING_DESIGN.md)
- [C4 contexto/contenedores/componentes de los ocho BC](docs/C4_MODELS.md)
- [Diagramas de clase UML por bounded context](docs/UML_DETAILED_8_BC.md)
- [Diseño físico de persistencia y limitaciones de FK](docs/PHYSICAL_DATA_MODEL.md)
- [Historias técnicas con aceptación Given/When/Then](docs/TECHNICAL_STORIES.md)
- [Integración de tercero, clima ambiente](docs/EXTERNAL_SERVICE.md)
- [Checklist de evidencias de despliegue TB2](docs/DEPLOYMENT_EVIDENCE.md)

La API REST tipada agrega operaciones semánticas para envíos en `/api/v1/operations/shipments`, junto con `/api/v1/operations/weather`; el contrato `/api/operations` que ya consume Angular **no cambia**. Certificados ahora exigen lecturas dentro del periodo real entre despacho y recepción, con muestras para todos los productos. Errores HTTP localizados y tipificados. GitHub Actions incluye build, tests, migración PostgreSQL, health y OpenAPI.

**Condición de entrega:** en este entorno no están instalados Maven ni Docker y Maven Central no es accesible; por tanto no se puede afirmar que compilación completa, integración Angular, migración y despliegue hayan pasado. Ejecuta el workflow y conserva evidencia real. El PDF pide más que código backend (informe, landing, colaboración, validación y sustentación), por lo que un ZIP no garantiza 20/20.

---


**Adaptado al proyecto Angular Refrio y al ejemplo de clase `learning-center-platform`.**
La arquitectura replica sus capas de agregados, objetos de valor, repositorios y
adaptadores de persistencia, extendida a **ocho bounded contexts** y con casos de
uso funcionales. `shared` no cuenta como contexto.

**Módulos:** `IAM`, `Inventory`, `Operations`, `Alerting`, `Traceability`,
`Analytics`, `Public Portal` y `Events`.

Este proyecto ofrece dos APIs en paralelo:

- **API compatible** `/api/operations`: conserva la forma `{ "args": [...] }` y el
  snapshot del frontend actual para la integración inmediata.
- **API DDD tipada** `/api/v1/...`: controladores por contexto, modelos y repositorios
  tipados, y autorización según rol/destinatario.

Documentación nueva:

- [`docs/DDD_ARCHITECTURE.md`](docs/DDD_ARCHITECTURE.md) — correspondencia de clases y
  contexto con el proyecto de ejemplo; capas, puertos, adaptadores y límites.
- [`docs/API_CONTRACT.md`](docs/API_CONTRACT.md) — endpoints y ejemplos de requests.
- [`docs/INTELLIJ_GITHUB.md`](docs/INTELLIJ_GITHUB.md) — IntelliJ IDEA, Maven Wrapper,
  Docker, integración Angular y publicación manual en GitHub.

**Persistencia real:** IAM utiliza una tabla JPA específica; los otros siete contextos
utilizan repositorios tipados con adaptadores JPA sobre un almacén JSON compartido
para mantener compatibilidad con el `db.json` existente. La base de datos es
PostgreSQL con Flyway, pero esta etapa **no representa una normalización completa
por agregado**. Los detalles y trade-offs se explican en `docs/DDD_ARCHITECTURE.md`.

**Estado de verificación:** dominio compilado en Java 21; pruebas JUnit e
integración provistas para su ejecución con Maven/CI; no se declara aquí que
el servidor entero haya sido compilado ni desplegado públicamente.

---

## 1. Mapa de bounded contexts

| Bounded context | Dominio | Capa de aplicación | Infraestructura y API |
|---|---|---|---|
| `iam` | `UserAccount`, `UserAccountRepository` | autenticación y altas | JPA adapter, JWT Bearer, BCrypt, controladores |
| `inventory` | `Allocation`, `FefoPolicy` | comandos de inventario orquestados | consultas de productos, lotes, lecturas y mermas |
| `operations` | `ShipmentPolicy` | `OperationsApplicationService` y transacciones | endpoints de despachos y clientes |
| `alerting` | `IncidentState` | gestión mediante comando | consultas de alertas e incidentes |
| `traceability` | `TemperatureEvidence` | `CertificationService` | certificados y consulta QR |
| `analytics` | `OperationalMetrics` | `AnalyticsService` | métricas calculadas |
| `publicportal` | `LeadStatus` | `LeadApplicationService` | registro de contactos |
| `events` | `EventType` | emisión desde casos de uso | consulta de auditoría operativa |
| `shared` | excepciones, puertos compartidos | utilidades | persistencia, candado transaccional, manejo de errores |

### Importante sobre las decisiones de arquitectura

Es un **monolito modular orientado a DDD**. El flujo FEFO y las reglas del despacho se implementan con servicios de dominio puros; los controladores no manipulan directamente las reglas de negocio. La infraestructura usa PostgreSQL y Flyway.

Para mantener exactamente la forma flexible de `db.json` del frontend sin inventar campos, las colecciones operativas se guardan como **documentos JSON en una tabla transaccional** (`resource_records`), separadas mediante la columna `resource`. IAM sí utiliza un esquema relacional propio (`app_users`). Es una decisión de compatibilidad que permite importar los 11 recursos operativos; **no equivale a un modelo relacional normalizado para cada agregado**. En una evolución posterior pueden extraerse repositorios JPA específicos sin cambiar los contratos REST.

Se utiliza `operation_mutex` con bloqueo pesimista para serializar escrituras que afectan a varios recursos: inventario, lotes, despacho y auditoría, incluso cuando existen varias réplicas del backend. No reemplaza otras medidas de escalabilidad y no pretende ofrecer un sistema financiero de inventarios.

## 2. Requisitos

- Java 21 JDK.
- Maven 3.9+ (IntelliJ puede descargarlo al importar `pom.xml`).
- Docker Desktop con Docker Compose, o PostgreSQL 16.
- Un secreto JWT **de al menos 32 bytes**; no se incluye ningún secreto de producción.

## 3. Ejecutar en IntelliJ IDEA (Windows)

1. `File > Open` y selecciona la carpeta `refrio-backend` o `pom.xml`.
2. Selecciona **Project SDK = Java 21** y recarga Maven.
3. Copia `.env.example` a `.env` y reemplaza los secretos.
4. Abre la terminal integrada de IntelliJ y ejecuta `docker compose up --build -d`.
5. Visita `http://localhost:8080/actuator/health` para verificar el estado de la API y `http://localhost:8080/swagger-ui.html` para consultar Swagger UI.

Para ejecutar Java directamente sin Docker: inicia PostgreSQL, crea la base `refrio`, configura `DATABASE_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, etc., en la configuración Run/Debug de IntelliJ y ejecuta `RefrioApplication`.

**Importante:** las variables dentro del archivo `.env` son utilizadas automáticamente por Docker Compose, pero IntelliJ **no** las incorpora automáticamente en la configuración de ejecución Java. Cárgalas expresamente en `Run > Edit Configurations > Environment variables`.

## 4. Usuarios y seguridad

- `POST /api/users` registra usuarios. Las contraseñas se almacenan con BCrypt, **no** como los textos de `db.json`.
- `POST /api/session` devuelve un JWT `Bearer` firmado con HMAC SHA-256.
- `GET /api/session` requiere `Authorization: Bearer <token>` y devuelve el perfil actual sin contraseña.
- En producción, configura `BOOTSTRAP_ADMIN_USERNAME` y `BOOTSTRAP_ADMIN_PASSWORD` para crear el Supervisor inicial, o provisiona cuentas de forma controlada. Su contraseña debe medir al menos 12 caracteres.
- Por defecto, el formulario público no puede crear cuentas con rol `Supervisor` para evitar la elevación de privilegios. Puedes habilitarlo **solo en un entorno académico de demostración** con `ALLOW_PUBLIC_SUPERVISOR_SIGNUP=true`.
- En `SEED_DEMO=true`, además de los registros de muestra, se crea un usuario de prueba `bodega-demo` con una contraseña propia definida en `DEMO_RETAIL_PASSWORD` (mínimo 12 caracteres). Se vincula el cliente demo a ese usuario real, sin importar las contraseñas en claro de `db.json`.

Ejemplos PowerShell:

```powershell
$body = @{ username='admin'; password='TU_PASSWORD_ADMIN' } | ConvertTo-Json
$session = Invoke-RestMethod -Uri 'http://localhost:8080/api/session' -Method Post -ContentType 'application/json' -Body $body
$headers = @{ Authorization = "Bearer $($session.token)" }
Invoke-RestMethod -Uri 'http://localhost:8080/api/operations' -Headers $headers
```

Para registrar un Minorista:

```powershell
$signup = @{ username='bodega'; email='bodega@example.com'; password='Cambiar-123456'; firstName='Ana'; lastName='Rios'; role='Minorista'; subscriptionPlan='Basico'; sede='Surco' } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/api/users' -Method Post -ContentType 'application/json' -Body $signup
```

## 5. API que consume Angular

| Método | Endpoint | Acceso | Uso |
|---|---|---|---|
| `POST` | `/api/session` | Público | Inicio de sesión |
| `GET` | `/api/session` | Token | Usuario actual |
| `POST` | `/api/users` | Público, rol limitado | Registro |
| `GET` | `/api/operations` | Token | Snapshot de colecciones, filtrado por rol |
| `POST` | `/api/operations/saveProduct` | Token | Crear/editar producto |
| `POST` | `/api/operations/registerBatch` | Token | Registrar lote |
| `POST` | `/api/operations/createShipment` | Supervisor | Asignación FEFO + picking + reserva |
| `POST` | `/api/operations/advanceShipment` | Supervisor | Cambiar estado del envío |
| `POST` | `/api/operations/receiveShipment` | Destinatario | Aceptar/observar/rechazar |
| `POST` | `/api/operations/recordLoss` | Token | Descontar merma |
| `POST` | `/api/operations/recordTemperature` | Token | Guardar telemetría |
| `POST` | `/api/operations/updateIncident` | Token | Flujo de incidencia |
| `GET` | `/api/inventory`, `/api/batches`, `/api/telemetry` | Token | Consulta de inventario |
| `GET` | `/api/alerts`, `/api/incidents`, `/api/events` | Token | Alertas y auditoría |
| `POST` | `/api/clients`, `/api/suppliers` | Supervisor | Alta de clientes y proveedores |
| `GET` | `/api/shipments`, `/api/clients`, `/api/suppliers` | Token | Catálogos y entregas |
| `POST` | `/api/certificates/shipment/{id}` | Supervisor/destinatario | Registro de evidencia de frío |
| `GET` | `/api/certificates`, `/api/certificates/qr/{code}` | Token | Consulta de certificados |
| `GET` | `/api/analytics/summary` | Token | Métricas derivadas de datos reales |
| `POST` | `/api/leads` | Público | Solicitudes comerciales |
| `GET` | `/api/leads` | Supervisor | Consulta de contactos |

**Forma exacta de los comandos:** `POST /api/operations/{command}` con cuerpo `{ "args": [ ... ] }`; el resultado es `{ "result": ..., "data": { ... } }`, tal como espera `OperationsApiService` del frontend.

Ejemplo de alta de lote (como Supervisor):

```powershell
$payload = @{ args = @( @{ productId='PRD-001'; quantity=10; expirationDate='2026-12-31'; supplier='Fresh Farms Co.'; location='Rack A'; receptionDate='2026-11-09' } ) } | ConvertTo-Json -Depth 8
Invoke-RestMethod -Uri 'http://localhost:8080/api/operations/registerBatch' -Method Post -Headers $headers -ContentType 'application/json' -Body $payload
```

## 6. Reglas funcionales

1. **FEFO:** solo lotes de Supervisor con stock positivo, fecha válida y futura, no bloqueados ni descartados. Se asigna primero el vencimiento más próximo.
2. **Picking:** se rechaza el escaneo si el orden de IDs es distinto al plan de lotes.
3. **Reserva de inventario:** al confirmar un nuevo envío, se descuentan sus unidades del almacén de origen dentro de la misma transacción.
4. **Estados:** `Preparing` → `In Transit` → `Awaiting Receipt` → `Delivered`/`Rejected`. El estado `Delayed` también puede pasar a `Awaiting Receipt`.
5. **Recepción:** el destinatario no puede incorporar más de lo enviado; acepta, observa o rechaza. Los rechazos no incrementan inventario de destino. La recepción no puede aplicarse dos veces.
6. **Merma:** se valida que la cantidad exista, se descuenta del lote y se registra evento.
7. **Telemetría:** se registran lecturas con fecha y audiencia. El monitoreo y generación de alertas derivados del frontend siguen siendo cálculos del lado Angular; la API persiste alertas almacenadas e incidencias.
8. **Certificación:** requiere envío recibido y al menos una lectura; la huella SHA-256 expuesta es un **checksum de integridad, no firma electrónica jurídicamente válida ni PDF certificado**.

## 7. Importar `db.json` del repositorio Angular

Para importar el contenido real del JSON Server sin copiar usuarios ni contraseñas:

1. Copia el `db.json` del frontend a un lugar accesible para Java.
2. Configura `SEED_DEMO=true`, `DEMO_RETAIL_PASSWORD` con 12+ caracteres y `SEED_FILE=/ruta/absoluta/db.json` (si ejecutas desde IntelliJ).
3. Reinicia; el importador añade registros no existentes por `(resource, id)` y no sobrescribe los ya guardados.
4. En Docker, monta el archivo de solo lectura en el contenedor API y configura `SEED_FILE` a la ruta interna (por ejemplo `/opt/refrio/seed/db.json`).

El importador omite `users`, `$schema` y recursos desconocidos por diseño. Como muchas fechas del `db.json` original eran anteriores a noviembre de 2026, los lotes ya caducados **no** serán seleccionados por FEFO hasta que registres otros elegibles.

## 8. Integración y despliegue

Consulta `integration/README.md` para conectar Angular sin cambiar su `OperationsApiService`. En desarrollo, configura `ng serve --proxy-config proxy.conf.json`. En una web estática con GitHub Pages, define la URL HTTPS del backend en `environment.ts` o usa un reverse proxy externo; `/api` relativo por sí solo no llegará a Java.

En producción se recomienda ejecutar el contenedor detrás de un proxy HTTPS, usar secretos externos, copias de seguridad y no habilitar `SEED_DEMO` ni `ALLOW_PUBLIC_SUPERVISOR_SIGNUP`.

```bash
# Compilación y pruebas en un entorno con Maven y acceso a repositorios de dependencias
mvn clean verify
# Contenedores
cp .env.example .env  # antes, modifica valores reales
# tras editar .env
docker compose up --build -d
# Logs
docker compose logs -f api
```

## 9. Publicación en GitHub

Crea un repositorio **separado**, por ejemplo `refrio-backend` (mantén `refrio-frontend` independiente), y desde la terminal de IntelliJ:

```bash
git init
git add .
git commit -m "feat: implement Refrio modular DDD backend"
git branch -M main
git remote add origin https://github.com/TU-ORGANIZACION/refrio-backend.git
git push -u origin main
```

La carpeta `.env` y `target/` se excluyen mediante `.gitignore`. El workflow de CI usa Java 21 y ejecuta las pruebas Maven.

## 10. Alcance verificado y limitaciones

- Se incluyen pruebas unitarias de FEFO y del flujo de recepción, además de pruebas de integración para persistencia y autenticación.
- Se implementa el contrato principal del frontend Angular **sin modificar su repositorio original**.
- Un entorno de pruebas con Maven/Internet y una base PostgreSQL es necesario para confirmar una compilación integral y un despliegue real; una revisión estática no lo sustituye.
- Las entidades operativas se persisten como documentos JSON por compatibilidad; una evolución a agregados tipados y tablas propias por contextos sería adecuada para un sistema empresarial de mayor escala.
- No se implementan SMS, WhatsApp, ingestión automática IoT, generación real de PDF, firma digital acreditada ni pagos: ninguna de esas integraciones está contratada por el frontend proporcionado.


## Verificación local sin Maven Central

```powershell
python scripts/check_ddd_structure.py
python scripts/check_source_integrity.py
python scripts/verify_domain.py
# Con checkout local del frontend:
python scripts/check_frontend_contract.py ../refrio-frontend
# Cuando Maven Central esté disponible:
.\mvnw.cmd clean verify
```

Los scripts Python + javac comprueban archivos, puertos/imports y reglas de dominio, pero **no prueban REST/JPA completos**. Para eso se requiere Maven, los JUnit y una base de datos de pruebas. La CI incluye un segundo job de migración PostgreSQL real y autenticación de cuentas demo, si se publica en GitHub Actions.
