package ar.edu.unvime.api_blank.favorito.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;
import ar.edu.unvime.api_blank.favorito.dto.FavoritoRequest;
import ar.edu.unvime.api_blank.favorito.dto.FavoritoResponse;
import ar.edu.unvime.api_blank.favorito.exception.FavoritoNotFoundException;
import ar.edu.unvime.api_blank.favorito.repository.FavoritoRepository;
import ar.edu.unvime.api_blank.lista.exception.ListaNotFoundException;
import ar.edu.unvime.api_blank.lista.repository.ListaRepository;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;
    private final ListaRepository listaRepository;

    public FavoritoService(FavoritoRepository repository, ListaRepository listaRepository) {
        this.repository = repository;
        this.listaRepository = listaRepository;
    }

    public FavoritoResponse crear(FavoritoRequest request) {
        validarListaExiste(request.listaId());
        Favorito favorito = new Favorito(
            null,
            request.productoId(),
            request.nota(),
            Instant.now(),
            request.listaId()
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
        validarListaExiste(request.listaId());
        favorito.actualizar(request.productoId(), request.nota(), request.listaId());
        return FavoritoResponse.from(repository.save(favorito));
    }

    public void eliminar(Long id) {
        findOrThrow(id);
        repository.deleteById(id);
    }

    private Favorito findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new FavoritoNotFoundException(id));
    }

    private void validarListaExiste(Long listaId) {
        if (!listaRepository.existsById(listaId)) {
            throw new ListaNotFoundException(listaId);
        }
    }
}
