package ar.edu.unvime.api_blank.favorito.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;

@Repository
public class InMemoryFavoritoRepository implements FavoritoRepository {

    private final ConcurrentMap<Long, Favorito> favoritos = new ConcurrentHashMap<>();

    @Override
    public Favorito save(Favorito favorito) {
        favoritos.put(favorito.getId(), favorito);
        return favorito;
    }

    @Override
    public List<Favorito> findAll() {
        return new ArrayList<>(favoritos.values());
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return Optional.ofNullable(favoritos.get(id));
    }

    @Override
    public void deleteById(Long id) {
        favoritos.remove(id);
    }
}
