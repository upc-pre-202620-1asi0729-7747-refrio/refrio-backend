# Servicio externo — ambient weather via Open-Meteo

Función de apoyo: consulta temperatura ambiente y humedad relativa en coordenadas de Lima u otra ruta. No representa temperatura de una unidad refrigerada ni acredita conservación de alimentos.

Endpoint autenticado: `GET /api/v1/operations/weather?latitude=-12.05&longitude=-77.04`.

Arquitectura DDD: `GeoCoordinates` y `AmbientWeather` (value objects) → `WeatherProvider` (puerto de aplicación) → `OpenMeteoWeatherProvider` (adaptador de infraestructura HTTP Java 21) → `WeatherController`. El proveedor está desacoplado para poder reemplazarse y probarse mediante un fake. Timeout de conexión 4s; request 7s. Errores upstream controlados como HTTP 503.

Config: `EXTERNAL_WEATHER_ENABLED=true`, `EXTERNAL_WEATHER_BASE_URL=https://api.open-meteo.com`. Desactivar con `false` en ambientes sin licencia/red.

**Licencias:** revisar la licencia y restricciones comerciales vigentes de Open-Meteo antes de desplegar una solución comercial SaaS. En el entorno actual no se ha hecho una petición real al proveedor; se incluyen pruebas con fake y un servidor HTTP local, sin conexión externa.

**Frontend:** el servicio Angular de integración de ejemplo está en `integration/angular/refrio-weather.service.ts`. El repositorio GitHub original NO ha sido modificado: integrar ese servicio en una vista real para cumplir el requisito end-to-end.
