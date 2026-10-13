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

y confirmar que la última versión aplicada es `V4__lista_id_obligatorio`.

## Documentación

- Swagger UI: `http://localhost:8080/swagger-ui.html`
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

La operación de mover favoritos entre listas se ejecuta en `ListaService.moverFavoritos` con `@Transactional`. Si se quitara esa anotación y una de las escrituras (por ejemplo, reasignar favoritos o borrar la lista origen) fallara después de otra ya confirmada, la base quedaría parcialmente modificada: algunos favoritos podrían haber cambiado de lista y la lista origen seguir existiendo, generando inconsistencia de datos. Con la transacción, la operación se ejecuta como una sola unidad atómica: o todo queda aplicado o todo se revierte.

## Verificación

```powershell
./mvnw.cmd test
```

> La aplicación principal necesita PostgreSQL corriendo en localhost:5432 para arrancar con la configuración de producción. Los tests usan H2 para validar el contexto sin depender de la base local.


