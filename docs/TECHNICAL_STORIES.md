# Technical Stories API — Refrio (formato Given/When/Then)

Estas historias técnicas tienen el rol Developer. Corresponden a capacidades de backend, no sustituyen las User Stories del Product Backlog real del equipo.

| ID | Como… deseo… para… | Aceptación (Given/When/Then) |
|---|---|---|
| TS-IAM-01 | Como Developer deseo autenticar usuarios JWT para ofrecer sesiones seguras. | Given credenciales válidas, When POST /api/session, Then respuesta 200 y token; Given token ausente, When GET /api/session, Then 401. |
| TS-INV-01 | Como Developer deseo consultar y registrar lotes FEFO para preparar despachos sin inventario vencido. | Given lote con fecha válida, When registro, Then queda persistido; Given lote vencido, When se planifica picking, Then se omite o rechaza. |
| TS-OPS-01 | Como Developer deseo crear y recibir despachos tipados para integrar Angular de forma REST. | Given lote disponible y escaneado correcto, When POST /api/v1/operations/shipments, Then 201 e inventario reducido; Given usuario Minorista, Then 403 al crear. |
| TS-ALR-01 | Como Developer deseo revisar incidencias y documentar acciones correctivas. | Given incidencia abierta, When PATCH con transición válida, Then cambia el estado; Given transición ilegal, Then 400 sin cambio. |
| TS-TRA-01 | Como Developer deseo certificar temperatura por envío para no usar lecturas anteriores. | Given envío recibido con lecturas para cada producto dentro del intervalo, When certificar, Then crea comprobante; Given lecturas anteriores al despacho, Then rechaza. |
| TS-ANA-01 | Como Developer deseo obtener métricas calculadas por rol para generar reportes confiables. | Given un usuario autorizado, When GET /api/v1/analytics/reports/operational, Then retorna métricas derivadas de la BD. |
| TS-PUB-01 | Como Developer deseo registrar contactos comerciales para atender interesados sin login. | Given email válido, When POST /api/leads, Then 201; Given email inválido, Then 400 y mensaje localizado. |
| TS-EVT-01 | Como Developer deseo consultar eventos de dominio para auditar operaciones. | Given una mutación válida, When se confirma la transacción, Then se guarda evento; Given rollback, Then no se guarda. |
