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
import ar.edu.unvime.api_blank.security.AccesoDenegadoException;
import ar.edu.unvime.api_blank.security.ContextoSeguridad;
import ar.edu.unvime.api_blank.lista.domain.Lista;

@Service
public class FavoritoService {

    private final FavoritoRepository repository;
    private final ListaRepository listaRepository;
    private final ContextoSeguridad contextoSeguridad;

    public FavoritoService(
        FavoritoRepository repository,
        ListaRepository listaRepository,
        ContextoSeguridad contextoSeguridad
    ) {
        this.repository = repository;
        this.listaRepository = listaRepository;
        this.contextoSeguridad = contextoSeguridad;
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
        List<Favorito> favoritos = contextoSeguridad.esAdmin()
            ? repository.findAll()
            : repository.findByListaUsuarioId(contextoSeguridad.usuarioActualId());
        return favoritos.stream().map(FavoritoResponse::from).toList();
    }

    public FavoritoResponse obtener(Long id) {
        return FavoritoResponse.from(findOrThrow(id));
    }

    public FavoritoResponse actualizar(Long id, FavoritoRequest request) {
        Favorito favorito = findOrThrow(id);
        validarListaPropiaOFallar(favorito.getListaId());
        validarListaPropiaOFallar(request.listaId());
        favorito.actualizar(request.productoId(), request.nota(), request.listaId());
        return FavoritoResponse.from(repository.save(favorito));
    }

    public void eliminar(Long id) {
        validarListaPropiaOFallar(findOrThrow(id).getListaId());
        repository.deleteById(id);
    }

    private Favorito findOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new FavoritoNotFoundException(id));
    }

    private void validarListaExiste(Long listaId) {
        validarListaPropiaOFallar(listaId);
    }

    private void validarListaPropiaOFallar(Long listaId) {
        Lista lista = listaRepository.findById(listaId).orElseThrow(() -> new ListaNotFoundException(listaId));
        if (!contextoSeguridad.esAdmin()
            && !contextoSeguridad.usuarioActualId().equals(lista.getUsuarioId())) {
            throw new AccesoDenegadoException();
        }
    }
}
