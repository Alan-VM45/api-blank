package ar.edu.unvime.api_blank.usuario.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("El email ya está registrado: " + email);
    }
}
