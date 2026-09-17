package ar.edu.unvime.api_blank.favorito.domain;

import java.time.Instant;

public class Favorito {

    private Long id;
    private Long productoId;
    private String nota;
    private Instant fechaAgregado;

    public Favorito(Long id, Long productoId, String nota, Instant fechaAgregado) {
        this.id = id;
        this.productoId = productoId;
        this.nota = nota;
        this.fechaAgregado = fechaAgregado;
    }

    public Long getId() { return id; }
    public Long getProductoId() { return productoId; }
    public String getNota() { return nota; }
    public Instant getFechaAgregado() { return fechaAgregado; }
    public void actualizar(Long productoId, String nota) {
        this.productoId = productoId;
        this.nota = nota;
    }
}
