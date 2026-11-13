# Matriz de pruebas — ocho bounded contexts

El ZIP anterior sólo incluía cuatro carpetas en `src/test/java`. Este paquete incluye pruebas de los ocho contextos y una suite adicional `architecture` para garantizar la estructura DDD.

| Contexto | Clases de prueba | Objetivo |
|---|---|---|
| iam | `AuthenticationIntegrationTest`, `IamApplicationServiceTest`, `UserAccountDomainTest` | Registro, autenticación, rol, contraseña y perfil |
| inventory | `FefoPolicyTest`, `InventoryAggregatesTest`, `InventoryCommandServiceTest` | Reglas FEFO, lotes, límites de stock, mermas, temperatura, comandos |
| operations | `OperationsIntegrationTest`, `OperationsQueryServiceTest`, `ShipmentAggregateTest`, `ShipmentPolicyTest` | Transiciones, recepción, permisos y transacción de despacho |
| alerting | `AlertingApplicationServiceTest`, `IncidentAggregateTest` | Máquina de estados, autorización por audiencia, persistencia |
| traceability | `CertificateIssuancePolicyTest`, `CertificationServiceTest`, `TemperatureEvidenceTest`, `TraceabilityQueryServiceTest` | Emisión, lecturas válidas, fingerprint y consulta por QR |
| analytics | `AnalyticsApplicationServiceTest`, `AnalyticsReportAggregateTest` | Indicadores calculados, consistencia y acceso por usuario |
| publicportal | `LeadAggregateTest`, `LeadApplicationServiceTest` | Validación de leads, avance comercial y alta pública |
| events | `EventsApplicationServiceTest`, `OperationalEventAggregateTest` | Registro inmutable, publicación transaccional y visibilidad |

**Pruebas transversales**: `BoundedContextDomainTest`, `BoundedContextRepositoryIntegrationTest`, `DddPackageStructureTest`.

## Ejecución en IntelliJ / PowerShell

```powershell
cd refrio-backend
python scripts/check_ddd_structure.py
python scripts/verify_domain.py
.\mvnw.cmd clean verify
```

`verify_domain.py` usa sólo Java 21: compila los modelos DDD puros y ejecuta 27 validaciones. No prueba Spring Boot, PostgreSQL ni HTTP. `mvnw.cmd clean verify` ejecuta los tests JUnit y de integración que requieren las dependencias Maven.

## Condición de verificación

En el entorno de generación se ejecutaron `check_ddd_structure.py` y `verify_domain.py` satisfactoriamente. No fue posible ejecutar Maven ni Docker debido a la ausencia de Maven/Docker y de acceso al repositorio Maven Central. No se asegura que las pruebas completas o el despliegue hayan pasado; deben comprobarse en un equipo o CI con las herramientas instaladas.


## v4: pruebas adicionales y verificaciones importantes

- `ShipmentRestIntegrationTest`: API semántica POST, rol Supervisor vs Minorista, token ausente, estado de despacho, localización de validaciones.
- `OpenApiSmokeTest`: metadata OpenAPI y presencia de endpoint POST /api/v1/operations/shipments.
- `ShipmentTemperaturePolicyTest`: lecturas únicamente dentro del intervalo y por cada producto.
- `GeoCoordinatesTest`: límites geográficos y NaN.
- `ExternalWeatherServiceTest`: contrato de puerto externo sustituible con fake.

**No declarar suites Maven superadas hasta contar con reportes `target/surefire-reports/` o GitHub Actions.**

- `OpenMeteoWeatherProviderHttpTest`: servidor HTTP local del JDK, parseo de JSON y error 503 de proveedor; no usa internet.

- `check_source_integrity.py`: revisa packages, imports locales, enlaces y que el dominio no importe Spring/Jackson/JPA.
- `check_frontend_contract.py`: compara comandos de `operations.store.ts` contra el `switch` de compatibilidad Java. Se ejecuta solo cuando el checkout frontend está disponible.
