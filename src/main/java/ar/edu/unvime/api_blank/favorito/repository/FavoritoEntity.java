package ar.edu.unvime.api_blank.favorito.repository;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import ar.edu.unvime.api_blank.lista.repository.ListaEntity;

@Entity
@Table(name = "favoritos")
public class FavoritoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(nullable = false, length = 500)
    private String nota;

    @Column(name = "fecha_alta", nullable = false)
    private Instant fechaAlta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_id", nullable = false)
    private ListaEntity lista;

    protected FavoritoEntity() {
    }

    public FavoritoEntity(Long id, Long productoId, String nota, Instant fechaAlta, ListaEntity lista) {
        this.id = id;
        this.productoId = productoId;
        this.nota = nota;
        this.fechaAlta = fechaAlta;
        this.lista = lista;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Instant getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Instant fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public ListaEntity getLista() {
        return lista;
    }

    public void setLista(ListaEntity lista) {
        this.lista = lista;
    }

    public Long getListaId() {
        return lista != null ? lista.getId() : null;
    }
}
