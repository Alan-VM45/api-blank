package ar.edu.unvime.api_blank.lista.domain;

public class Lista {

    private Long id;
    private String nombre;

    public Lista(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
