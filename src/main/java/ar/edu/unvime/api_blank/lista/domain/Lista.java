package ar.edu.unvime.api_blank.lista.domain;

public class Lista {

    private Long id;
    private String nombre;
    private Long usuarioId;

    public Lista(Long id, String nombre) {
        this(id, nombre, null);
    }

    public Lista(Long id, String nombre, Long usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
