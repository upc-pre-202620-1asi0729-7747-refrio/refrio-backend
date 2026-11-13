// Ejemplo para reemplazar src/environments/environment.ts en el frontend.
// En producción en distinto dominio, el servidor debe permitir el origen del frontend.
export const environment = {
  production: true,
  serverBasePath: 'https://YOUR-BACKEND-DOMAIN.example.com/api'
};
