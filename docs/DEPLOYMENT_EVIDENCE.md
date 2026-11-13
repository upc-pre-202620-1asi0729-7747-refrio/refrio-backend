# Evidencias reales de release TB2 — instrucciones, no capturas fabricadas

Antes de declarar que Refrio está desplegado: 

1. Publicar repositorio `refrio-backend` en la organización pública, con commits de todos los integrantes mediante GitFlow (`develop`, `feature/...`, `release/...`, `hotfix/...`) y Conventional Commits.
2. Crear PostgreSQL, almacenar secretos de producción fuera del repositorio, configurar CORS para URL real de Angular, y activar HTTPS.
3. Con Java 21 y Maven: `./mvnw -B clean verify`. Guardar reportes de `target/surefire-reports`, y logs de compilación y dependencias.
4. Desplegar contenedor con variables `DATABASE_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, CORS y cuentas bootstrap seguras (opcional, no registrar contraseña en informe).
5. Capturar `/actuator/health`, `/v3/api-docs`, `/swagger-ui.html` con hora/URL legibles.
6. Demostrar vía Angular: alta de lote, FEFO, confirmación de picking, despacho, estado de envío, recepción parcial, merma, incidencia y certificado con telemetría del propio intervalo.
7. Capturar errores negativos: token ausente (401), rol no autorizado (403), FEFO inválido (400), JSON inválido (400), recurso inexistente (404), proveedor externo temporalmente caído (503).
8. Incluir tests de integración y evidencia de respuesta del servicio de terceros real, no datos simulados.
9. Integrar capturas y URLs públicas en Document Report: 5.2.X.4 Development, 5.2.X.5 Execution, 5.2.X.6 Services Documentation, 5.2.X.7 Deployment, 5.2.X.8 Collaboration.
10. Anexar explicaciones de los diagramas C4, UML, base de datos y EventStorming, incluyendo las limitaciones conocidas y las decisiones de implementación.

Si no se completan esos pasos, el código descargable por sí solo no prueba el cumplimiento del release final.
