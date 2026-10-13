package ar.edu.unvime.api_blank.favorito.dto;

import java.time.Instant;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;

public record FavoritoResponse(Long id, Long productoId, String nota, Instant fechaAgregado, Long listaId) {

    public static FavoritoResponse from(Favorito favorito) {
        return new FavoritoResponse(
            favorito.getId(),
            favorito.getProductoId(),
            favorito.getNota(),
            favorito.getFechaAgregado(),
            favorito.getListaId()
        );
    }
}
