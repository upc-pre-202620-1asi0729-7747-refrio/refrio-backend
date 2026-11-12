# Conexión con refrio-frontend

Repositorio revisado: https://github.com/upc-pre-202620-1asi0729-7747-refrio/refrio-frontend

## Local

1. Inicia esta API en `localhost:8080`.
2. Copia `frontend-proxy.conf.json` al directorio del frontend como `proxy.conf.json`.
3. Ejecuta: `npx ng serve --proxy-config proxy.conf.json`.
4. Mantén `serverBasePath: '/api'` en los `environment.ts` de Angular.
5. No ejecutes `npm run api` de JSON Server al mismo tiempo.

## Producción

- Si Angular y API usan el mismo dominio, configura el proxy reverso para enrutar `/api/**` al contenedor Java.
- Si se usan dominios diferentes (p. ej., Angular en GitHub Pages), cambia `src/environments/environment.ts` para usar la URL HTTPS absoluta del backend y reconstruye Angular.
- Incluye el origen del sitio Angular, sin barra al final, en `CORS_ORIGINS`.
- GitHub Pages **no ejecuta** una API Java ni proporciona un proxy `/api`.

## Contratos conservados

- `POST /api/session` produce `token`, `tokenType`, `expiresIn`, `role`.
- `GET /api/session` produce perfil actual.
- `POST /api/users` registra cuentas.
- `GET /api/operations` expone las colecciones del JSON Server.
- `POST /api/operations/{command}` recibe `{ "args": [...] }` y devuelve `{ "result": ..., "data": ... }`.

Diferencia intencional: registrar un usuario Supervisor desde un formulario público está deshabilitado por defecto por seguridad. En pruebas académicas puede activarse con `ALLOW_PUBLIC_SUPERVISOR_SIGNUP=true`.


## Opcional: consulta de clima ambiente (servicio de terceros)

El ejemplo `angular/refrio-weather.service.ts` utiliza el `serverBasePath` actual. Debes integrarlo en una vista Angular real (p. ej., detalles del envío), identificar claramente que se trata de **clima externo**, no de telemetría de la cámara frigorífica. Sin realizar esta integración y desplegarla no se puede acreditar el flujo de servicio externo completo.
