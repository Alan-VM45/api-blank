# TP1 - API REST y arquitectura en capas

API REST desarrollada con Spring Boot para el TP1 de Web II. No utiliza base de datos: los favoritos se guardan en memoria y se pierden al reiniciar la aplicación.

## Requisitos

- Java 25
- Maven (o el wrapper incluido)

## Ejecutar

En Windows:

```powershell
./mvnw.cmd spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Documentación

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Health check: `GET http://localhost:8080/health`

## Endpoints principales

### Productos

Los productos se consultan desde DummyJSON y se transforman al DTO propio de la API.

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
	"nota": "Regalo de cumpleaños"
}
```

```http
GET /api/favoritos
GET /api/favoritos/1
PUT /api/favoritos/1
DELETE /api/favoritos/1
```

Una solicitud inválida, por ejemplo `{ "productoId": null, "nota": "" }`, responde `400 Bad Request` con el detalle de cada campo. Un id inexistente responde `404 Not Found`.

## Verificación

```powershell
./mvnw.cmd test
```

