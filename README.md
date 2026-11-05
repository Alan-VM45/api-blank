# TP2 - Persistencia, migraciones y arquitectura hexagonal

API REST desarrollada con Spring Boot y PostgreSQL, usando JPA/Hibernate y Flyway para persistir favoritos y listas.

## Requisitos

- Java 25
- Docker Desktop o PostgreSQL local
- Maven o el wrapper incluido

## Levantar PostgreSQL

### Opción 1: Docker

```powershell
docker compose up -d
```

Esto levanta una instancia local en `localhost:5432` con la base `api_blank`, usuario `api_blank` y contraseña `api_blank`.

### Opción 2: PostgreSQL local

Crear una base de datos llamada `api_blank` y configurar el mismo usuario/contraseña indicados en `src/main/resources/application.properties`.

## Ejecutar la aplicación

```powershell
./mvnw.cmd spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Migraciones

Flyway aplica automáticamente los scripts bajo `src/main/resources/db/migration`. Se pueden revisar con:

```sql
SELECT * FROM flyway_schema_history;
```

y confirmar que la última versión aplicada es `V6__add_usuario_id_a_listas`.

## Documentación

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Health check: `GET http://localhost:8080/health`

## Cambios respecto del TP1

En el TP1 los favoritos vivían en memoria en `InMemoryFavoritoRepository`. En este TP2 la capa de persistencia se reemplazó por JPA/Hibernate y un adapter encaminado al puerto `FavoritoRepository`, sin tocar el dominio ni el Service del caso de uso. Lo mismo ocurrió con `ListaRepository`: el dominio y la API siguen usando contratos, mientras que `FavoritoRepositoryAdapter` y `ListaRepositoryAdapter` son implementaciones concretas de infraestructura.

Esto fue posible porque `FavoritoRepository` y `ListaRepository` son puertos: una interfaz de contrato que permite intercambiar adaptadores (memoria, JPA, tests, etc.) sin cambiar el comportamiento de la lógica de negocio.

## Endpoints principales

### Productos

```http
GET /api/productos
GET /api/productos?limit=5&skip=0
GET /api/productos/1
```

### Favoritos

```http
POST /api/favoritos
Content-Type: application/json

{
  "productoId": 1,
  "nota": "Regalo de cumpleaños",
  "listaId": 1
}
```

```http
GET /api/favoritos
GET /api/favoritos/1
PUT /api/favoritos/1
DELETE /api/favoritos/1
```

### Listas

```http
POST /api/listas
Content-Type: application/json

{
  "nombre": "Regalos"
}
```

```http
GET /api/listas
GET /api/listas/1
GET /api/listas/1/favoritos
DELETE /api/listas/1
POST /api/listas/2/mover-favoritos
Content-Type: application/json

{
  "destinoId": 1
}
```

## Evolución del esquema

La migración `V4__lista_id_obligatorio.sql` se creó como una nueva versión en lugar de editar `V1`, `V2` o `V3` ya aplicadas. Flyway registra cada versión aplicada en `flyway_schema_history` y rechaza cambios sobre una migración ya ejecutada porque eso rompería la historia del esquema y podría causar inconsistencias en producción.

## Transacciones

La operación de mover favoritos entre listas se ejecuta en `ListaServiceImpl.moverFavoritos` con `@Transactional`. Si se quitara esa anotación y una de las escrituras (por ejemplo, reasignar favoritos o borrar la lista origen) fallara después de otra ya confirmada, la base quedaría parcialmente modificada: algunos favoritos podrían haber cambiado de lista y la lista origen seguir existiendo, generando inconsistencia de datos. Con la transacción, la operación se ejecuta como una sola unidad atómica: o todo queda aplicado o todo se revierte.

## Verificación

```powershell
.\mvnw.cmd clean test
```

> Para iniciar la aplicación, PostgreSQL debe estar disponible en `localhost:5432` o se deben configurar las variables de conexión. Las pruebas de integración usan PostgreSQL real mediante Testcontainers y requieren Docker.

## TP3

### Resumen

TP3 agrega registro de usuarios con contraseñas BCrypt, login y autenticación JWT, y los roles `USER` y `ADMIN`. Cada lista nueva pertenece al usuario autenticado; los favoritos se leen y modifican según la propiedad de su lista. El registro siempre crea un usuario `USER`: el cliente no puede autoasignarse el rol `ADMIN`.

### Endpoints

| Método | Endpoint | ¿Requiere token? | Códigos posibles |
|---|---|---:|---|
| GET | `/health` | No | 200 |
| GET | `/api/productos` | No | 200, 400, 502 |
| GET | `/api/productos/{id}` | No | 200, 400, 404, 502 |
| POST | `/api/auth/registro` | No | 201, 400, 409 |
| POST | `/api/auth/login` | No | 200, 400, 401 |
| POST | `/api/listas` | Sí | 201, 400, 401 |
| GET | `/api/listas` | Sí | 200, 401 |
| GET | `/api/listas/{id}` | Sí | 200, 401, 403, 404 |
| GET | `/api/listas/{id}/favoritos` | Sí | 200, 401, 403, 404 |
| DELETE | `/api/listas/{id}` | Sí | 204, 401, 403, 404, 409 |
| POST | `/api/listas/{origenId}/mover-favoritos` | Sí | 204, 400, 401, 403, 404 |
| POST | `/api/favoritos` | Sí | 201, 400, 401, 403, 404 |
| GET | `/api/favoritos` | Sí | 200, 401 |
| GET | `/api/favoritos/{id}` | Sí | 200, 401, 403, 404 |
| PUT | `/api/favoritos/{id}` | Sí | 200, 400, 401, 403, 404 |
| DELETE | `/api/favoritos/{id}` | Sí | 204, 401, 403, 404 |

### Autenticación y autorización

- **401 (autenticación):** `SecurityConfig` establece los endpoints públicos y requiere autenticación para el resto. `JwtAuthenticationFilter` valida el bearer token antes de que Spring Security atienda la solicitud. La autenticación se decide antes de conocer el recurso concreto.
- **403 (propiedad o rol):** la autorización por propiedad vive en el Service. En particular, `ListaServiceImpl.buscarPropiaOFallar` consulta el dueño de la lista; `FavoritoService` aplica la misma política a los favoritos y a su lista destino. No se duplica la regla en cada método del Controller ni se deja depender de un único punto de entrada. `@PreAuthorize` por sí solo no alcanza para comprobar una propiedad que requiere consultar la base. `ContextoSeguridad` desacopla los Services de Spring Security y permite probarlos con Mockito.
- Una lista ajena que existe devuelve **403**, no **404**, para distinguir una denegación de acceso de un identificador inexistente.
- Las listas anteriores al TP3 que conservan `usuario_id = NULL` solo son visibles y operables por `ADMIN`; no se asignan automáticamente a un usuario nuevo.

Para promover manualmente a un usuario:

```sql
UPDATE usuarios SET rol = 'ADMIN' WHERE email = 'usuario@example.com';
```

El rol se incluye en el JWT. Cerrá sesión y volvé a iniciar sesión para obtener un token con el rol actualizado.

### Configuración local y variables de entorno

| Variable | Default local |
|---|---|
| `PORT` | `8080` |
| `DB_URL` | `jdbc:postgresql://localhost:5432/api_blank` |
| `DB_USERNAME` | `api_blank` |
| `DB_PASSWORD` | `api_blank` |
| `JWT_SECRETO` | `dev-secreto-local-solo-para-desarrollo-cambiar-en-produccion-1234567890` |
| `JWT_VIGENCIA_MINUTOS` | `60` |

El valor JWT predeterminado es solo para desarrollo local. En producción configurá un secreto aleatorio de al menos 32 bytes mediante `JWT_SECRETO`; no guardes secretos reales en el repositorio.

### Pruebas y cobertura

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean verify
```

`clean test` ejecuta las unitarias sin iniciar Spring ni conectarse a una base. `clean verify` también ejecuta las clases `*IT` con PostgreSQL/Testcontainers, Flyway y el control de cobertura; requiere Docker disponible. El reporte HTML de JaCoCo queda en `target/site/jacoco/index.html` y los datos unitarios y de integración se combinan. El umbral actual de cobertura de líneas es 50%.

### Integración continua

En GitHub, abrí **Actions → CI → Compilar y probar**. El workflow ejecuta:

1. Compilación del proyecto y las pruebas (`test-compile`).
2. Pruebas unitarias (`test`).
3. Pruebas de integración y cobertura (`verify`).

El workflow agrega el resumen de cobertura de líneas y el artefacto `reporte-de-cobertura`. Para repetir una corrida, usá **Re-run jobs** en la ejecución de Actions. Corrida verde: **[pegar link a la ejecución de Actions]**.

### Despliegue en Render

1. Creá el servicio desde **New → Blueprint** y elegí este repositorio y la rama `tp3`.
2. Render usa `render.yaml` para crear el servicio Docker y la base de datos. Configurá `DB_URL` con la URL interna de PostgreSQL, por ejemplo `jdbc:postgresql://HOSTNAME_INTERNO:5432/NOMBRE_DB?sslmode=require`.
3. Dejá que Flyway aplique las migraciones V1–V6 y revisá los logs hasta ver **Your service is live**.
4. Verificá `GET /health` y la autenticación usando la URL pública.

URL pública: **[completar luego del alta en Render]**. Corrida verde de Actions: **[pegar link]**.

### Swagger

Abrí `http://localhost:8080/swagger-ui/index.html` (o `/swagger-ui/index.html` en la URL publicada). Usá **Authorize** y pegá el valor del token JWT sin anteponer `Bearer `; Swagger agrega el esquema bearer por vos.
