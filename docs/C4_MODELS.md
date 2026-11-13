# Diagramas de arquitectura C4 — fuentes editables

Estos diagramas son **fuentes Mermaid listas para exportar**, no capturas de una herramienta ni de un despliegue real.

## C4 nivel 1 — Contexto

```mermaid
flowchart LR
  supervisor[Supervisor de cadena de frío] --> refrio[Plataforma Refrio]
  retail[Usuario minorista] --> refrio
  visitor[Visitante comercial] --> refrio
  refrio --> open[Open-Meteo — clima ambiental]
```

## C4 nivel 2 — Contenedores

```mermaid
flowchart LR
  user[Usuarios] --> landing[Landing page — HTML/CSS/JS]
  user --> spa[Web App — Angular]
  landing --> spa
  spa --> api[Backend — Spring Boot/Java 21]
  api --> db[(PostgreSQL)]
  api --> weather[Open-Meteo Weather API]
  api --> swagger[OpenAPI 3.0 documentado por Springdoc]
```

Nota: landing y web app están representados como contenedores lógicos; comprobar sus URL y despliegues definitivos con el equipo.

## C4 nivel 3 — Componentes del contenedor Backend

```mermaid
flowchart TB
  rest[API REST Controllers / DTOs] --> iam[IAM]
  rest --> inventory[Inventory]
  rest --> operations[Operations]
  rest --> alerting[Alerting]
  rest --> traceability[Traceability]
  rest --> analytics[Analytics]
  rest --> public[Public Portal]
  rest --> events[Events]
  operations --> inventory
  operations --> alerting
  operations --> events
  inventory --> events
  traceability --> operations
  traceability --> inventory
  analytics --> inventory
  analytics --> operations
  public --> events
  iam --> db[(JPA / PostgreSQL)]
  inventory --> db
  operations --> db
  alerting --> db
  traceability --> db
  analytics --> db
  public --> db
  events --> db
  operations --> provider[WeatherProvider port]
  provider --> ext[OpenMeteoWeatherProvider adapter]
```

## C4 nivel 3 — componentes por bounded context

Cada sección siguiente representa un componente lógico. Los nombres de puertos y adaptadores corresponden a archivos Java reales.

### iam

```mermaid
flowchart LR
 Controller["IamController"] --> App["IamApplicationService"]
 App --> Aggregate["UserAccount"]
 App --> Port["UserAccountRepository"]
 Port --> Adapter["UserRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```

### inventory

```mermaid
flowchart LR
 Controller["InventoryV1Controller"] --> App["InventoryCommandService"]
 App --> Aggregate["InventoryProduct / StockBatch"]
 App --> Port["InventoryProductRepository / StockBatchRepository"]
 Port --> Adapter["InventoryProductRepositoryAdapter / StockBatchRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```

### operations

```mermaid
flowchart LR
 Controller["ShipmentCommandController"] --> App["OperationsApplicationService"]
 App --> Aggregate["ShipmentAggregate"]
 App --> Port["ShipmentRepository"]
 Port --> Adapter["ShipmentRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```

### alerting

```mermaid
flowchart LR
 Controller["AlertingV1Controller"] --> App["AlertingApplicationService"]
 App --> Aggregate["IncidentAggregate"]
 App --> Port["IncidentRepository"]
 Port --> Adapter["IncidentRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```

### traceability

```mermaid
flowchart LR
 Controller["TraceabilityV1Controller"] --> App["CertificationService"]
 App --> Aggregate["ColdChainCertificateAggregate"]
 App --> Port["CertificateRepository"]
 Port --> Adapter["CertificateRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```

### analytics

```mermaid
flowchart LR
 Controller["AnalyticsV1Controller"] --> App["AnalyticsReportService"]
 App --> Aggregate["AnalyticsReportAggregate"]
 App --> Port["AnalyticsReadRepository"]
 Port --> Adapter["AnalyticsReadRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```

### publicportal

```mermaid
flowchart LR
 Controller["PublicPortalV1Controller"] --> App["LeadApplicationService"]
 App --> Aggregate["LeadAggregate"]
 App --> Port["LeadRepository"]
 Port --> Adapter["LeadRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```

### events

```mermaid
flowchart LR
 Controller["EventsV1Controller"] --> App["EventsQueryService"]
 App --> Aggregate["OperationalEventAggregate"]
 App --> Port["OperationalEventRepository"]
 Port --> Adapter["OperationalEventRepositoryAdapter"]
 Adapter --> Database[(PostgreSQL)]
```
