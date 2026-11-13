# Abrir, ejecutar y publicar desde IntelliJ IDEA

## Importación

1. Extrae el ZIP sin perder carpetas; abre `refrio-backend/pom.xml` con **Open as Project**.
2. Configura **Java 21** como SDK; IntelliJ debe reconocer el proyecto como Maven.
3. Sin Maven del sistema, usa `mvnw.cmd` en Windows o `./mvnw` en Linux/macOS.
4. Para el backend se necesita PostgreSQL 16 (incluido en `compose.yaml`) y un secreto JWT.

## Configuración de entorno

Copia `.env.example` a `.env` y cambia `DB_PASSWORD`, `JWT_SECRET` y
`BOOTSTRAP_ADMIN_PASSWORD`. En Docker Compose se carga automáticamente. En
`Run > Edit Configurations` de IntelliJ debes añadir las variables al proceso Java
si deseas ejecutar directamente la clase `RefrioApplication`.

```powershell
# Desde PowerShell en refrio-backend/
Copy-Item .env.example .env
# Edita .env antes de continuar
./mvnw.cmd clean verify
# Con Docker Desktop iniciado:
docker compose up --build -d
docker compose logs -f api
```

Abre `http://localhost:8080/swagger-ui.html` y
`http://localhost:8080/actuator/health`.

## Frontend local

En el directorio del frontend, copia `integration/frontend-proxy.conf.json`
como `proxy.conf.json` y usa:

```powershell
npx ng serve --proxy-config proxy.conf.json
```

No ejecutes JSON Server y la API Spring Boot simultáneamente sobre la misma
ruta `/api`. El frontend consume `GET /api/operations` y
`POST /api/operations/{command}` sin modificaciones del servicio Angular.

## GitHub

**No se publica en el repositorio original automáticamente.** Sube la carpeta
con commits normales desde IntelliJ `Git > Commit`, `Git > Push` o terminal:

```bash
git init
git add .
git commit -m "feat: implement Refrio backend with eight DDD bounded contexts"
git branch -M main
git remote add origin https://github.com/TU-ORGANIZACION/refrio-backend.git
git push -u origin main
```

Si quieres alojarlo dentro del repositorio `refrio-frontend`, colócalo en
`backend/` en una rama `feature/refrio-backend-ddd`, y abre un PR. Para ese caso,
el workflow de CI debe ejecutarse con `working-directory: backend`, porque
el workflow incluido en este ZIP es para el repositorio backend autónomo.

## CI y publicación

GitHub Actions compila y ejecuta `./mvnw -B clean verify` y construye la imagen
Docker. Eso valida el código en CI, **no** despliega una instancia públicamente.
Puedes usar un VPS, Render, Railway o un contenedor en cualquier proveedor con
soporte para Java y PostgreSQL. Configura HTTPS, secrets y `CORS_ORIGINS`.

No se incluyen credenciales de proveedor ni secretos. Nunca subas `.env`.
