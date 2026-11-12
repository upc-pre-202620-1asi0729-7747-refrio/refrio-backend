# Architectural Decision Records (ADRs) — Refrio

## ADR-001: modular monolith with exactly eight bounded contexts

**Decision:** Java 21 Spring Boot modular monolith, aligned to the existing Angular functional boundaries. Shared infrastructure is not a ninth bounded context. **Tradeoff:** cross-context orchestration uses transactional application services and stable port interfaces rather than distributed messaging. This is practical for a university release, but components are not separately deployable microservices.

## ADR-002: preserve Angular JSON contract while exposing typed REST

**Decision:** preserve `/api/operations/{command}` while adding typed REST `/api/v1/operations/shipments`. The frontend currently polls a composite snapshot every five seconds. **Tradeoff:** large snapshots and a common `resource_records` JPA table create a persistence and throughput debt. Flyway V2 adds indexes and bounded-context read views without creating a second source of truth. **Next architecture step:** replace JSON adapters with normalized domain tables and FK constraints, migrate data incrementally while maintaining contract tests.

## ADR-003: certify only in-shipment sensor evidence

**Decision:** `ShipmentTemperaturePolicy` requires a finite temperature reading for every product inside the dispatch-to-receipt time window; do not use historical data from other shipments. **Tradeoff:** certificates can be refused if no adequate telemetry was collected. That is correct rather than fabricating evidence. Current data records are product-scoped, not cryptographically bound to a specific refrigerated vehicle. True third-party certification requires trusted sensor IDs, device signatures and logistics attestation.

## ADR-004: HTTP 401/403/404/503 and i18n

**Decision:** standardized JSON error `message` plus code/status/detail/path/timestamp and default English/Spanish resource bundles. **Tradeoff:** legacy domain exception descriptions continue in Spanish in the auxiliary `detail` field. Continue migrating all domain errors to coded localized messages.

## ADR-005: third-party weather as optional support

**Decision:** Open-Meteo through `WeatherProvider` port; never mix ambient weather with cargo temperature. **Tradeoff:** production license restrictions and external service availability apply; disabled in test profile, timed requests return 503 on failure.

## ADR-006: destinatario comprobado por una interfaz de contexto

`Operations` no almacena usuarios IAM ni acepta un `recipientId` inventado: `RecipientIdentityPort` verifica existencia/rol a través de `IamRecipientIdentityAdapter`. La importación demo asigna un ID real al cliente, evitando que un flujo exitoso dependa de `id=2`.
