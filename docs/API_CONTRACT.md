# Contratos REST de Refrio

## API heredada que el frontend utiliza hoy

| HTTP | URI | Payload | Función |
|---|---|---|---|
| POST | `/api/session` | `{ "username": "...", "password": "..." }` | JWT |
| GET | `/api/session` | Bearer | Perfil sin contraseña |
| POST | `/api/users` | Usuario + contraseña | Registro Minorista (Supervisor por configuración explícita) |
| GET | `/api/operations` | Bearer | Snapshot filtrado según rol |
| POST | `/api/operations/saveProduct` | `{ "args": [product, optionalId] }` | Producto |
| POST | `/api/operations/registerBatch` | `{ "args": [batch] }` | Alta de lote |
| POST | `/api/operations/createShipment` | `{ "args": [request] }` | FEFO, picking, reserva |
| POST | `/api/operations/advanceShipment` | `{ "args": [shipmentId] }` | Siguiente estado |
| POST | `/api/operations/receiveShipment` | `{ "args": [id, decision, quantities, notes] }` | Recepción |
| POST | `/api/operations/recordLoss` | `{ "args": [batchId, quantity, cause] }` | Merma |
| POST | `/api/operations/recordTemperature` | `{ "args": [productId, value] }` | Lectura |
| POST | `/api/operations/updateIncident` | `{ "args": [id, stage, input, subjectId, audience, snapshot] }` | Incidencia |

Todos los comandos compatibles devuelven `{ "result": ..., "data": { ... } }`.

## API versionada (repositorios y respuestas tipados)

| Contexto | HTTP | URI |
|---|---|---|
| IAM | GET | `/api/v1/iam/users/me` |
| Inventory | GET | `/api/v1/inventory/products` |
| Inventory | GET | `/api/v1/inventory/products/{id}` |
| Inventory | GET | `/api/v1/inventory/batches` |
| Inventory | GET | `/api/v1/inventory/batches/fefo` |
| Operations | GET | `/api/v1/operations/shipments` |
| Operations | GET | `/api/v1/operations/shipments/{id}` |
| Operations | GET | `/api/v1/operations/clients` |
| Operations | GET | `/api/v1/operations/suppliers` |
| Alerting | GET | `/api/v1/alerting/alerts` |
| Alerting | GET | `/api/v1/alerting/incidents` |
| Alerting | PATCH | `/api/v1/alerting/incidents/{id}` |
| Traceability | GET | `/api/v1/traceability/certificates` |
| Traceability | GET | `/api/v1/traceability/certificates/qr/{code}` |
| Analytics | GET | `/api/v1/analytics/reports/operational` |
| Public Portal | GET | `/api/v1/public-portal/leads` |
| Public Portal | PATCH | `/api/v1/public-portal/leads/{id}` |
| Events | GET | `/api/v1/events` |

Todos estos endpoints requieren JWT. Las vistas de usuarios Minorista filtran por
rol/destinatario; los leads y el catálogo comercial exigen Supervisor.

## Ejemplo de despacho

```json
{
  "args": [{
    "clientId": "CLI-001",
    "expectedRecipientId": "2",
    "origin": "Lima Central",
    "destination": "Surco",
    "driver": "Transportista",
    "vehicle": "ABC-123",
    "eta": "2026-11-15T10:00:00",
    "lines": [{"productId":"PRD-001","quantity":10}],
    "scanned": ["BT-4501"]
  }]
}
```

El código en `scanned` debe coincidir exactamente con los lotes elegidos por FEFO;
no debe suponerse que un lote vencido en noviembre de 2026 es apto.

## Seguridad y despliegue

- CORS se configura por `CORS_ORIGINS`.
- Angular en GitHub Pages necesita URL absoluta HTTPS del backend (no basta `/api`).
- GitHub Pages solo sirve contenido estático y no aloja aplicaciones Spring Boot.
- Contraseñas bcrypt, firma JWT HS256, roles comprobados en backend.
- Swagger: `/swagger-ui.html`; healthcheck: `/actuator/health`.


## API REST tipada de Operations (v4)

| Método | Ruta | Descripción | Seguridad |
|---|---|---|---|
| POST | `/api/v1/operations/shipments` | Crear despacho con líneas FEFO, picks escaneados y rol Supervisor | Bearer Supervisor |
| POST | `/api/v1/operations/shipments/{id}/advance` | Avanzar a siguiente estado permitido | Bearer Supervisor |
| POST | `/api/v1/operations/shipments/{id}/receipt` | Aceptar, observar o rechazar cantidades por lote | Bearer destinatario/Supervisor |
| GET | `/api/v1/operations/weather?latitude=-12.05&longitude=-77.04` | Clima exterior; Open-Meteo (no temperatura de carga) | Bearer |

### Solicitud Create Shipment (201)

```json
{
  "clientId": "CLI-001", "origin": "Lima", "destination": "Surco",
  "driver": "Juan", "vehicle": "REF-001", "eta": "2026-11-12T16:00",
  "lines": [{ "productId": "PRD-001", "quantity": 12 }],
  "scanned": ["BT-4501"]
}
```

La secuencia de códigos escaneados debe corresponder con el plan FEFO. `POST /api/v1/operations/shipments` responde `{ "id": "ENV-...", "status": "Preparing" }`. Los estados válidos de una recepción son `accepted`, `observed`, `rejected`.

### Solicitud Receipt (200)

```json
{ "decision": "accepted", "quantities": [{ "batchId": "BT-4501", "productId": "PRD-001", "quantity": 12 }], "notes": "" }
```

### Respuestas de error y i18n

El backend preserva `message` para el Angular existente y añade `code`, `status`, `detail`, `path` y `timestamp`. Mensajes generales en inglés por defecto y español mediante `Accept-Language: es-419`. Algunos detalles heredados del dominio permanecen en español (deuda de i18n documentada). Estados: 400 para validación de request/invariantes, 401 para no autenticado, 403 para acceso prohibido, 404 para recursos físicos inexistentes, 503 para proveedor externo indisponible.

**La documentación viva es `/v3/api-docs`/`/swagger-ui.html`**; requiere ejecutar la aplicación. La lista completa de operaciones de los ocho contexts se genera desde las anotaciones Spring MVC y springdoc.
