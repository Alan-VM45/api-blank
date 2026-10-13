package ar.edu.unvime.api_blank.favorito.domain;

import java.time.Instant;

public class Favorito {

    private Long id;
    private Long productoId;
    private String nota;
    private Instant fechaAgregado;
    private Long listaId;

    public Favorito(Long id, Long productoId, String nota, Instant fechaAgregado, Long listaId) {
        this.id = id;
        this.productoId = productoId;
        this.nota = nota;
        this.fechaAgregado = fechaAgregado;
        this.listaId = listaId;
    }

    public Long getId() { return id; }
    public Long getProductoId() { return productoId; }
    public String getNota() { return nota; }
    public Instant getFechaAgregado() { return fechaAgregado; }
    public Long getListaId() { return listaId; }
    public void actualizar(Long productoId, String nota, Long listaId) {
        this.productoId = productoId;
        this.nota = nota;
        this.listaId = listaId;
    }
}
