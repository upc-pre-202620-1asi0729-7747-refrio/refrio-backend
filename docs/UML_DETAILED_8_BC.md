# UML de diseño detallado — los ocho bounded contexts

Fuente editable Mermaid, extraída de las clases reales de la versión 1.1.0. Las propiedades representadas son el núcleo del aggregate y no todos los miembros triviales. `-` indica propiedad encapsulada de un `record` y `+` operación pública. Las relaciones entre BC son **referencias por identificador**, no asociaciones JPA navegables implícitas.

## IAM

```mermaid
classDiagram
    class UserAccount {
      -Long id
      -String username
      -String email
      -String passwordHash
      -String role
      -String subscriptionPlan
    }
    class Username { -String value }
    class EmailAddress { -String value }
    class UserAccountRepository { <<interface>>
      +byUsername(String) Optional~UserAccount~
      +byId(Long) Optional~UserAccount~
      +save(UserAccount) UserAccount
    }
    class IamApplicationService {
      +register(RegisterUserCommand) Map
      +signIn(SignInCommand) Map
    }
    IamApplicationService --> UserAccountRepository
    UserAccount ..> Username
    UserAccount ..> EmailAddress
```

## Inventory

```mermaid
classDiagram
    class InventoryProduct {
      -String id
      -String name
      -String stock
      -String category
      -String temp
      +availableUnits() int
      +withStockAdjustment(int) InventoryProduct
      +withTemperature(double) InventoryProduct
    }
    class StockBatch {
      -String id
      -String productId
      -int quantity
      -String expirationDate
      -String audience
      +canDispatch(LocalDate) boolean
      +debit(int) StockBatch
    }
    class FefoPolicy {
      +plan(List, String, int, LocalDate) List~Allocation~
      +validatePicking(List, List) void
    }
    class InventoryCommandService {
      +create(CreateProductCommand) InventoryProduct
      +register(RegisterBatchCommand) StockBatch
      +recordLoss(RecordLossCommand) InventoryLoss
    }
    StockBatch "0..*" --> "1" InventoryProduct : productId
    InventoryCommandService ..> FefoPolicy
    InventoryCommandService --> StockBatch
```

## Operations

```mermaid
classDiagram
    class ShipmentAggregate {
      -String id
      -String status
      -String recipientId
      -List~Allocation~ allocations
      -List~StatusEntry~ timeline
      -Receipt receipt
      +advance() ShipmentAggregate
      +receive(String, List, String, String) ShipmentAggregate
      +canReceive(Long, boolean) boolean
    }
    class ShipmentPolicy {
      +advance(String) String
      +validateReceipt(String, boolean, List, List, String, String) void
    }
    class ShipmentRepository {
      <<interface>>
      +findById(String) Optional~ShipmentAggregate~
      +save(ShipmentAggregate) ShipmentAggregate
    }
    class OperationsApplicationService {
      +snapshot(AuthenticatedUser) ObjectNode
      +execute(String, ArrayNode, AuthenticatedUser) ObjectNode
    }
    ShipmentAggregate "1" o-- "0..*" Allocation
    ShipmentAggregate ..> ShipmentPolicy
    OperationsApplicationService --> ShipmentRepository
```

## Alerting

```mermaid
classDiagram
    class IncidentAggregate {
      -String id
      -String status
      -String cause
      -String action
      -String verification
      +open(String, String, String, String) IncidentAggregate
      +transition(String, String, String, String, String, String) IncidentAggregate
    }
    class IncidentState {
      <<enumeration>>
      IN_REVIEW
      ACTION_RECORDED
      RESOLVED
    }
    class IncidentRepository { <<interface>> }
    class AlertingApplicationService { +update(String, String, String, String, String, String, String, String, AuthenticatedUser) IncidentAggregate }
    IncidentAggregate ..> IncidentState
    AlertingApplicationService --> IncidentRepository
```

## Traceability

```mermaid
classDiagram
    class ColdChainCertificateAggregate {
      -String id
      -String shipmentId
      -Double minTemp
      -Double maxTemp
      -Double avgTemp
      -Integer samples
      -String digitalSignature
      +hasReadings() boolean
      +isGenericRangeCompliant() boolean
    }
    class ShipmentTemperaturePolicy {
      +forShipment(Set, Instant, Instant, List) TemperatureEvidence
    }
    class CertificationService { +certify(String, AuthenticatedUser) ObjectNode }
    class CertificateRepository { <<interface>> }
    CertificationService ..> ShipmentTemperaturePolicy
    CertificationService --> CertificateRepository
    CertificationService --> ColdChainCertificateAggregate
```

## Analytics

```mermaid
classDiagram
    class AnalyticsReportAggregate {
      -String audience
      -String generatedAt
      -OperationalMetrics metrics
      -Percentage deliveryRate
      +create(String, OperationalMetrics) AnalyticsReportAggregate
    }
    class OperationalMetrics { -int productCount
      -int shipmentCount
      -int deliveredCount
    }
    class AnalyticsReadRepository { <<interface>> }
    class AnalyticsReportService { +generate(AuthenticatedUser) AnalyticsReportAggregate }
    AnalyticsReportAggregate *-- OperationalMetrics
    AnalyticsReportService --> AnalyticsReadRepository
```

## Public Portal

```mermaid
classDiagram
    class LeadAggregate {
      -String id
      -String companyName
      -String email
      -String status
      +submit(String, String, String, String, String, String) LeadAggregate
      +moveTo(LeadStatus) LeadAggregate
    }
    class LeadStatus { <<enumeration>>
      NEW
      CONTACTED
      QUALIFIED
      CLOSED
    }
    class ContactEmail { -String value }
    class LeadApplicationService { +submit(String, String, String, String, String, String) ObjectNode }
    class LeadRepository { <<interface>> }
    LeadAggregate ..> ContactEmail
    LeadAggregate ..> LeadStatus
    LeadApplicationService --> LeadRepository
```

## Events

```mermaid
classDiagram
    class OperationalEventAggregate {
      -String id
      -String name
      -String aggregateId
      -String occurredAt
      -String outcome
      -String actor
      +visibleTo(String, boolean) boolean
      +userAction(String, String, String, String, String, String, String) OperationalEventAggregate
    }
    class EventOutcome { <<enumeration>>
      SUCCESS
      WARNING
      BLOCKED
      FAILURE
    }
    class OperationalEventRepository { <<interface>> }
    class OperationalEventPublisher { +publish(String, String, String, AuthenticatedUser, String, String, String) OperationalEventAggregate }
    OperationalEventAggregate ..> EventOutcome
    OperationalEventPublisher --> OperationalEventRepository
```

Estos diagramas no reemplazan las capturas y explicaciones que el enunciado pide realizar con las herramientas del curso; la fuente Mermaid debe ser revisada visualmente antes de insertarla en el Document Report.
