# Diagramas de clases UML — modelo de implementación

Nombres basados en el código Java presente. Relaciones esquemáticas para el informe: ampliar atributos y visibilidad consultando los sources en IntelliJ antes de capturar los diagramas definitivos.

## iam

```mermaid
classDiagram
  class IamController
  class IamApplicationService
  class UserAccount
  class UserAccountRepository { <<interface>> }
  class UserRepositoryAdapter
  IamController --> IamApplicationService
  IamApplicationService --> UserAccountRepository
  IamApplicationService ..> UserAccount
  UserRepositoryAdapter ..|> UserAccountRepository
```

## inventory

```mermaid
classDiagram
  class InventoryV1Controller
  class InventoryCommandService
  class InventoryProduct
  class InventoryProductRepository { <<interface>> }
  class InventoryProductRepositoryAdapter
  InventoryV1Controller --> InventoryCommandService
  InventoryCommandService --> InventoryProductRepository
  InventoryCommandService ..> InventoryProduct
  InventoryProductRepositoryAdapter ..|> InventoryProductRepository
```

## operations

```mermaid
classDiagram
  class ShipmentCommandController
  class OperationsApplicationService
  class ShipmentAggregate
  class ShipmentRepository { <<interface>> }
  class ShipmentRepositoryAdapter
  ShipmentCommandController --> OperationsApplicationService
  OperationsApplicationService --> ShipmentRepository
  OperationsApplicationService ..> ShipmentAggregate
  ShipmentRepositoryAdapter ..|> ShipmentRepository
```

## alerting

```mermaid
classDiagram
  class AlertingV1Controller
  class AlertingApplicationService
  class IncidentAggregate
  class IncidentRepository { <<interface>> }
  class IncidentRepositoryAdapter
  AlertingV1Controller --> AlertingApplicationService
  AlertingApplicationService --> IncidentRepository
  AlertingApplicationService ..> IncidentAggregate
  IncidentRepositoryAdapter ..|> IncidentRepository
```

## traceability

```mermaid
classDiagram
  class TraceabilityV1Controller
  class CertificationService
  class ColdChainCertificateAggregate
  class CertificateRepository { <<interface>> }
  class CertificateRepositoryAdapter
  TraceabilityV1Controller --> CertificationService
  CertificationService --> CertificateRepository
  CertificationService ..> ColdChainCertificateAggregate
  CertificateRepositoryAdapter ..|> CertificateRepository
```

## analytics

```mermaid
classDiagram
  class AnalyticsV1Controller
  class AnalyticsReportService
  class AnalyticsReportAggregate
  class AnalyticsReadRepository { <<interface>> }
  class AnalyticsReadRepositoryAdapter
  AnalyticsV1Controller --> AnalyticsReportService
  AnalyticsReportService --> AnalyticsReadRepository
  AnalyticsReportService ..> AnalyticsReportAggregate
  AnalyticsReadRepositoryAdapter ..|> AnalyticsReadRepository
```

## publicportal

```mermaid
classDiagram
  class PublicPortalV1Controller
  class LeadApplicationService
  class LeadAggregate
  class LeadRepository { <<interface>> }
  class LeadRepositoryAdapter
  PublicPortalV1Controller --> LeadApplicationService
  LeadApplicationService --> LeadRepository
  LeadApplicationService ..> LeadAggregate
  LeadRepositoryAdapter ..|> LeadRepository
```

## events

```mermaid
classDiagram
  class EventsV1Controller
  class EventsQueryService
  class OperationalEventAggregate
  class OperationalEventRepository { <<interface>> }
  class OperationalEventRepositoryAdapter
  EventsV1Controller --> EventsQueryService
  EventsQueryService --> OperationalEventRepository
  EventsQueryService ..> OperationalEventAggregate
  OperationalEventRepositoryAdapter ..|> OperationalEventRepository
```
