package ar.edu.unvime.api_blank.favorito.service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;
import ar.edu.unvime.api_blank.favorito.dto.FavoritoRequest;
import ar.edu.unvime.api_blank.favorito.dto.FavoritoResponse;
import ar.edu.unvime.api_blank.favorito.exception.FavoritoNotFoundException;
import ar.edu.unvime.api_blank.favorito.repository.FavoritoRepository;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;
    private final AtomicLong nextId = new AtomicLong(1);

    public FavoritoService(FavoritoRepository repository) {
        this.repository = repository;
    }

    public FavoritoResponse crear(FavoritoRequest request) {
        Favorito favorito = new Favorito(
            nextId.getAndIncrement(),
            request.productoId(),
            request.nota(),
            Instant.now()
        );
        return FavoritoResponse.from(repository.save(favorito));
    }

    public List<FavoritoResponse> listar() {
        return repository.findAll().stream().map(FavoritoResponse::from).toList();
    }

    public FavoritoResponse obtener(Long id) {
        return FavoritoResponse.from(findOrThrow(id));
    }

    public FavoritoResponse actualizar(Long id, FavoritoRequest request) {
        Favorito favorito = findOrThrow(id);
        favorito.actualizar(request.productoId(), request.nota());
        return FavoritoResponse.from(repository.save(favorito));
    }

    public void eliminar(Long id) {
        findOrThrow(id);
        repository.deleteById(id);
    }

    private Favorito findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new FavoritoNotFoundException(id));
    }
}
