package ar.edu.unvime.api_blank.usuario.domain;

public class Usuario {

    private Long id;
    private String email;
    private String passwordHash;
    private Rol rol;

    public Usuario(Long id, String email, String passwordHash, Rol rol) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Rol getRol() {
        return rol;
    }
}
