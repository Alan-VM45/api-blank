package ar.edu.unvime.api_blank.lista.exception;

public class ListaNotFoundException extends RuntimeException {
    public ListaNotFoundException(Long id) {
        super("Lista no encontrada: " + id);
    }
}
