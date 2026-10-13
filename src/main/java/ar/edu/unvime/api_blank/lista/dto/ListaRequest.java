package ar.edu.unvime.api_blank.lista.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ListaRequest(
    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 100, message = "nombre no puede superar los 100 caracteres")
    String nombre
) {
}
