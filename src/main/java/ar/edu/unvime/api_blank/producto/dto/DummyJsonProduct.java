package ar.edu.unvime.api_blank.producto.dto;

import java.math.BigDecimal;

public record DummyJsonProduct(
    Long id,
    String title,
    String description,
    BigDecimal price,
    String category,
    String thumbnail
) {
}
