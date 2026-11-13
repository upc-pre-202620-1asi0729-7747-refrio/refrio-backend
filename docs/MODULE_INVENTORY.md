# Inventario actualizado de los ocho bounded contexts

| Bounded context | Java en `main` | Aggregates | Value objects | Puertos | Adaptadores | REST controllers | Tests | Commands |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| iam | 19 | 1 | 3 | 1 | 1 | 2 | 3 | 2 |
| inventory | 24 | 4 | 2 | 4 | 4 | 2 | 3 | 4 |
| operations | 15 | 3 | 2 | 2 | 2 | 3 | 4 | 0 |
| alerting | 12 | 2 | 1 | 2 | 2 | 2 | 2 | 1 |
| traceability | 10 | 1 | 1 | 1 | 1 | 2 | 4 | 0 |
| analytics | 9 | 1 | 1 | 1 | 1 | 2 | 2 | 0 |
| publicportal | 10 | 1 | 1 | 1 | 1 | 2 | 2 | 1 |
| events | 9 | 1 | 1 | 1 | 1 | 2 | 2 | 0 |

**Los ocho contextos tienen pruebas propias.** `architecture` es una suite transversal, no otro contexto.

**Total Java de producción:** 123. **Total clases de prueba:** 25.
