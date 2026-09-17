package ar.edu.unvime.api_blank.favorito.exception;

public class FavoritoNotFoundException extends RuntimeException {

    public FavoritoNotFoundException(Long id) {
        super("No existe el favorito " + id);
    }
}
