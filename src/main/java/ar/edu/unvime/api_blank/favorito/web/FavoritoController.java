package ar.edu.unvime.api_blank.favorito.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ar.edu.unvime.api_blank.favorito.dto.FavoritoRequest;
import ar.edu.unvime.api_blank.favorito.dto.FavoritoResponse;
import ar.edu.unvime.api_blank.favorito.service.FavoritoService;

@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Favoritos almacenados en memoria")
public class FavoritoController {

    private final FavoritoService service;

    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crea un favorito")
    public ResponseEntity<FavoritoResponse> crear(
        @Valid @RequestBody FavoritoRequest request,
        UriComponentsBuilder uriBuilder
    ) {
        FavoritoResponse response = service.crear(request);
        return ResponseEntity.created(uriBuilder.path("/api/favoritos/{id}")
            .buildAndExpand(response.id()).toUri()).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista todos los favoritos")
    public ResponseEntity<List<FavoritoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un favorito")
    public ResponseEntity<FavoritoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un favorito")
    public ResponseEntity<FavoritoResponse> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody FavoritoRequest request
    ) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un favorito")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
