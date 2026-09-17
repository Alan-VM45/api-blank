package ar.edu.unvime.api_blank.producto.web;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ar.edu.unvime.api_blank.producto.dto.ProductoResponse;
import ar.edu.unvime.api_blank.producto.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catalogo consultado desde DummyJSON")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Lista los productos del catalogo externo")
    public ResponseEntity<List<ProductoResponse>> listar(
        @RequestParam(defaultValue = "20") Integer limit,
        @RequestParam(defaultValue = "0") Integer skip
    ) {
        return ResponseEntity.ok(productoService.listar(limit, skip));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un producto por su identificador")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtener(id));
    }
}
