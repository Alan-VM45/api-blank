package ar.edu.unvime.api_blank.lista.exception;

public class ListaConFavoritosException extends RuntimeException {
    public ListaConFavoritosException(Long id) {
        super("La lista " + id + " tiene favoritos asociados y no puede eliminarse");
    }
}
