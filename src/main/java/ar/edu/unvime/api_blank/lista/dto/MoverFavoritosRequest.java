package ar.edu.unvime.api_blank.lista.dto;

import jakarta.validation.constraints.NotNull;

public record MoverFavoritosRequest(
    @NotNull(message = "destinoId es obligatorio")
    Long destinoId
) {
}
