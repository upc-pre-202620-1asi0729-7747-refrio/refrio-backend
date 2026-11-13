# Refrio · Evaluación verificable frente al Final Project Statement 1ASI0729 (2026-20)

Base: *Final Project Statement ABET v1*, curso **Desarrollo de Aplicaciones Open Source**, 44 páginas. La rúbrica de trabajo final contempla evidencia de equipo, producto desplegado y sustentación; **no contiene una fórmula que permita convertir una cantidad de archivos Java en 20/20**. El profesor determina la nota.

## Matriz de requisitos: evidencia actual frente a pendiente

| Exigencia del enunciado | Estado del backend v4 | Evidencia / acción obligatoria |
|---|---|---|
| Java + Spring Boot + Spring Data JPA | Código preparado y versión 1.1.0 | `pom.xml`, `src/main/`, comprobar `./mvnw clean verify` |
| API propia REST integrada con Angular | API compatible + endpoints semánticos, **no integración E2E verificada** | `docs/API_CONTRACT.md`; desplegar y probar el frontend de GitHub Pages conectado al servidor |
| Ocho bounded contexts DDD | Modelos y tests para los ocho | `docs/DDD_ARCHITECTURE.md`, tests arquitectura, scripts |
| Aggregates, Commands, Queries y Domain Events | Presentes y documentados | `docs/EVENTSTORMING_DESIGN.md`; capturar sesión real en Miro/FigJam para el informe |
| Diagramas C4 contexto/contenedores/componentes | Fuentes Mermaid preparadas | `docs/C4_MODELS.md`, revisar y trasladar a herramienta del curso si el docente exige capturas |
| UML y diagramas de datos por bounded context | Fuentes Mermaid preparadas; storage mayormente documental | `docs/UML_DETAILED_8_BC.md`, `docs/PHYSICAL_DATA_MODEL.md`; la normalización por agregado sigue pendiente |
| OpenAPI/Swagger, endpoints y requests/responses | Springdoc configurado; test Swagger agregado | `/swagger-ui.html`, `/v3/api-docs`; generar capturas reales |
| Pruebas unitarias y de integración/aceptación | Fuentes de tests en 8 contextos; nuevos HTTP/smoke tests | `docs/TEST_MATRIX.md`; ejecutar JUnit y conservar reportes reales |
| Integración con servicio de terceros | Adaptador Open-Meteo programado | `/api/v1/operations/weather`, ver permisos de proveedor, demostrar desde cliente |
| Internacionalización English / Latin American Spanish | Mensajes comunes en ambos idiomas | `src/main/resources/messages*.properties`; falta traducir todos los detalles legacy de dominio y validar i18n del conjunto de apps |
| A11y e i18n Angular y Landing Page | **No verificadas en este ZIP** | Auditar frontend y landing, ARIA, formularios, navegación y traducciones |
| Despliegue final en cloud | Docker y GitHub Actions preparados; **no desplegado aquí** | URL pública, capturas health, OpenAPI y escenarios reales |
| GitFlow, conventional commits, SemVer, aportes | Guía y workflow incluidos; **historial real pendiente** | Publicar repo propio de web services, PRs/commits reales de cada miembro |
| Informe Markdown, videos, participación, entrevistas | **Fuera del backend** | Repositorio del informe, video, PPTX, PDF, matriz ABET, evidencia de entrevistas del equipo |
| Sustentación | **No verificable a partir de fuentes** | Cada integrante debe explicar sin guion las reglas FEFO, DDD, transacciones, APIs, errores y seguridad |

## Juicio de calidad

**No se puede garantizar ni afirmar 20/20** únicamente con este proyecto. Es una base técnica mucho más cercana al enunciado, pero siguen siendo bloqueantes: compilación Maven de punta a punta, integración real con la versión publicada de Angular, despliegue público, evidencias reales del proceso, documentación formal requerida y defensa individual. El diseño actual conserva un almacén documental JPA compartido para compatibilidad Angular: es una deuda arquitectónica para la calidad de base de datos y debe sustentarse o migrarse antes de asegurar excelencia.

## Pasos de comprobación estricta

1. `python scripts/check_ddd_structure.py`
2. `python scripts/verify_domain.py`
3. `./mvnw clean verify` y revisión de `target/surefire-reports/` (NO se ejecutó aquí por falta de Maven Central)
4. `docker compose up --build -d` sobre entorno con Docker y conexión a la base
5. Comprobar `GET /actuator/health`, `GET /v3/api-docs`, IAM, FEFO, despacho, recepción, certificados e indicadores desde Angular
6. Capturar las URL reales de GitHub, CI, Swagger y despliegue en las secciones 5.2.4.4–5.2.4.8 del informe TB2.

_Páginas de referencia del PDF: 19–21 (DDD/C4/UML/DB), 22–28 (evidencias), 30–31 (i18n/tecnología), 34–35 (TB2 y despliegue)._

## Seed reproducible para demostración

Al activar `SEED_DEMO=true` se exige `DEMO_RETAIL_PASSWORD` (12+ caracteres), se crea el usuario `bodega-demo` con BCrypt y se asigna el cliente `CLI-001` a su ID real. De este modo el despacho no puede dirigirse a un usuario fantasma. El comando de creación verifica por el puerto `RecipientIdentityPort` que el destinatario exista y posea rol `Minorista`.
