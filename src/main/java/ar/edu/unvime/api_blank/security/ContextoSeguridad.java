package ar.edu.unvime.api_blank.security;

public interface ContextoSeguridad {
    Long usuarioActualId();
    boolean esAdmin();
}
