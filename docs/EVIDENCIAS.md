# Evidencias de entrega TP3

- [x] `.\mvnw.cmd clean verify` en verde: guardar captura en `docs/evidencias/` y conservar [mvn-clean-verify.txt](./evidencias/mvn-clean-verify.txt).
- [ ] Reporte JaCoCo: captura de `target/site/jacoco/index.html`.
- [x] Link a la corrida verde de GitHub Actions: https://github.com/Alan-VM45/api-blank/actions/runs/42068065426
- [x] CI en rojo (test roto a propósito): https://github.com/Alan-VM45/api-blank/actions/runs/42064875587 — captura: [ci-rojo.png](./evidencias/ci-rojo.png)
      CI en verde (revert): https://github.com/Alan-VM45/api-blank/actions/runs/42068065426 — captura: [ci-verde.png](./evidencias/ci-verde.png)
- [ ] Logs de Render con Flyway V1–V6 y el mensaje **Your service is live**.
- [ ] URL pública: **[completar]** y captura de `GET /health` con HTTP 200.
- [ ] Capturas desde la URL pública: `GET /api/listas` con HTTP 401 sin token y HTTP 200 con token.
- [ ] Capturas de una lista ajena con HTTP 403 y de un acceso `ADMIN` con HTTP 200.
- [ ] Captura de Swagger con los endpoints de autenticación y el candado en listas/favoritos.

Guardar las capturas en `docs/evidencias/*.png`.
