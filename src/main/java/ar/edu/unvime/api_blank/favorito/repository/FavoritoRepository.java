package ar.edu.unvime.api_blank.favorito.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;

public interface FavoritoRepository {
    Favorito save(Favorito favorito);
    List<Favorito> findAll();
    Optional<Favorito> findById(Long id);
    void deleteById(Long id);
}
