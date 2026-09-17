package ar.edu.unvime.api_blank.favorito.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FavoritoRequest(
    @NotNull(message = "productoId es obligatorio")
    @Positive(message = "productoId debe ser positivo")
    Long productoId,

    @NotBlank(message = "nota es obligatoria")
    @Size(max = 500, message = "nota no puede superar los 500 caracteres")
    String nota
) {
}
