package ar.edu.unvime.api_blank.lista.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ar.edu.unvime.api_blank.favorito.domain.Favorito;
import ar.edu.unvime.api_blank.favorito.dto.FavoritoResponse;
import ar.edu.unvime.api_blank.lista.dto.ListaRequest;
import ar.edu.unvime.api_blank.lista.dto.ListaResponse;
import ar.edu.unvime.api_blank.lista.dto.MoverFavoritosRequest;
import ar.edu.unvime.api_blank.lista.service.ListaService;

@RestController
@RequestMapping("/api/listas")
@Tag(name = "Listas", description = "Agrupación de favoritos")
public class ListaController {

    private final ListaService service;

    public ListaController(ListaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crea una lista")
    public ResponseEntity<ListaResponse> crear(
        @Valid @RequestBody ListaRequest request,
        UriComponentsBuilder uriBuilder
    ) {
        ListaResponse response = service.crear(request);
        return ResponseEntity.created(uriBuilder.path("/api/listas/{id}")
            .buildAndExpand(response.id()).toUri()).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista todas las listas")
    public ResponseEntity<List<ListaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una lista por id")
    public ResponseEntity<ListaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/{id}/favoritos")
    @Operation(summary = "Obtiene los favoritos de una lista")
    public ResponseEntity<List<FavoritoResponse>> favoritosDeLista(@PathVariable Long id) {
        List<FavoritoResponse> response = service.favoritosDeLista(id).stream()
            .map(FavoritoResponse::from)
            .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina una lista vacía")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{origenId}/mover-favoritos")
    @Operation(summary = "Mueve todos los favoritos de una lista a otra y elimina la lista origen")
    public ResponseEntity<Void> moverFavoritos(@PathVariable Long origenId, @Valid @RequestBody MoverFavoritosRequest request) {
        service.moverFavoritos(origenId, request);
        return ResponseEntity.noContent().build();
    }
}
