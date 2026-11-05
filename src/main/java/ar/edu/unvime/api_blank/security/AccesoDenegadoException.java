package ar.edu.unvime.api_blank.security;

public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException() {
        super("No tiene permisos para acceder a este recurso");
    }
}
