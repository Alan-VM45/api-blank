# Evidencias de entrega TP3

- [x] `.\mvnw.cmd clean verify` en verde: salida [mvn-clean-verify.txt](./evidencias/mvn-clean-verify.txt) y captura del CI en verde [ci-verde.png](./evidencias/ci-verde.png).
- [x] Reporte JaCoCo: captura [jacoco.png](./evidencias/jacoco.png).
- [x] Link a la corrida verde de GitHub Actions: https://github.com/Alan-VM45/api-blank/actions/runs/42068065426 — captura [ci-verde.png](./evidencias/ci-verde.png).
- [x] Captura de CI en rojo (test roto a propósito) y luego en verde (revert): [ci-rojo.png](./evidencias/ci-rojo.png), [ci-verde.png](./evidencias/ci-verde.png). Corridas: [roja](https://github.com/Alan-VM45/api-blank/actions/runs/42064875587), [verde](https://github.com/Alan-VM45/api-blank/actions/runs/42068065426).
- [x] Logs de Render con Flyway V1–V6 y "Your service is live": [render-flyway.png](./evidencias/render-flyway.png), [render_live.png](./evidencias/render_live.png).
- [x] URL pública https://api-blank-lbnp.onrender.com + `GET /health` 200: captura [api-blank_health.png](./evidencias/api-blank_health.png) y respuesta [01-health.txt](./evidencias/01-health.txt).
- [x] `GET /api/listas` 401 sin token y 200 con token (desde URL pública): [02-listas-sin-token-401.txt](./evidencias/02-listas-sin-token-401.txt), [06-listas-con-token-200.txt](./evidencias/06-listas-con-token-200.txt).
- [x] Lista ajena 403 / ADMIN 200: 403 desde la URL pública [07-lista-ajena-403.txt](./evidencias/07-lista-ajena-403.txt); ADMIN 200 verificado por el test de integración `ListaControllerIT.obtener_listaAjenaComoAdmin_devuelve200` (ver [mvn-clean-verify.txt](./evidencias/mvn-clean-verify.txt) y la corrida verde de CI).
- [x] Swagger con endpoints de autenticación y candado en listas/favoritos: [swagger.png](./evidencias/swagger.png).

## Respuestas de la API pública

- Registro 201: [03-registro-201.txt](./evidencias/03-registro-201.txt).
- Login 200 (token redactado): [04-login-200.txt](./evidencias/04-login-200.txt).
- Crear lista 201: [05-crear-lista-201.txt](./evidencias/05-crear-lista-201.txt).
- Login inválido 401: [08-login-invalido-401.txt](./evidencias/08-login-invalido-401.txt).
