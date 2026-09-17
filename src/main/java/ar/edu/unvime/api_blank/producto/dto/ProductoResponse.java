package ar.edu.unvime.api_blank.producto.dto;

import java.math.BigDecimal;

public record ProductoResponse(
    Long id,
    String nombre,
    String descripcion,
    BigDecimal precio,
    String categoria,
    String imagen
) {
}
