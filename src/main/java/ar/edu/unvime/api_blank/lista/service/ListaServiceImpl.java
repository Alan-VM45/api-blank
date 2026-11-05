package ar.edu.unvime.api_blank.lista.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;
import ar.edu.unvime.api_blank.favorito.repository.FavoritoRepository;
import ar.edu.unvime.api_blank.lista.domain.Lista;
import ar.edu.unvime.api_blank.lista.dto.ListaRequest;
import ar.edu.unvime.api_blank.lista.dto.ListaResponse;
import ar.edu.unvime.api_blank.lista.dto.MoverFavoritosRequest;
import ar.edu.unvime.api_blank.lista.exception.ListaConFavoritosException;
import ar.edu.unvime.api_blank.lista.exception.ListaNotFoundException;
import ar.edu.unvime.api_blank.lista.repository.ListaRepository;
import ar.edu.unvime.api_blank.security.AccesoDenegadoException;
import ar.edu.unvime.api_blank.security.ContextoSeguridad;

@Service
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;
    private final ContextoSeguridad contextoSeguridad;

    public ListaServiceImpl(
        ListaRepository listaRepository,
        FavoritoRepository favoritoRepository,
        ContextoSeguridad contextoSeguridad
    ) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
        this.contextoSeguridad = contextoSeguridad;
    }

    @Override
    @Transactional
    public ListaResponse crear(ListaRequest request) {
        Lista lista = new Lista(null, request.nombre(), contextoSeguridad.usuarioActualId());
        return ListaResponse.from(listaRepository.save(lista));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListaResponse> listar() {
        List<Lista> listas = contextoSeguridad.esAdmin()
            ? listaRepository.findAll()
            : listaRepository.findByUsuarioId(contextoSeguridad.usuarioActualId());
        return listas.stream().map(ListaResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ListaResponse obtener(Long id) {
        return ListaResponse.from(buscarPropiaOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorito> favoritosDeLista(Long listaId) {
        buscarPropiaOFallar(listaId);
        return favoritoRepository.findByListaId(listaId);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Lista lista = buscarPropiaOFallar(id);
        if (!favoritoRepository.findByListaId(lista.getId()).isEmpty()) {
            throw new ListaConFavoritosException(id);
        }
        listaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void moverFavoritos(Long origenId, MoverFavoritosRequest request) {
        if (request.destinoId() == null) {
            throw new IllegalArgumentException("destinoId es obligatorio");
        }
        if (origenId.equals(request.destinoId())) {
            throw new IllegalArgumentException("La lista origen y destino deben ser distintas");
        }

        Lista origen = buscarPropiaOFallar(origenId);
        Lista destino = buscarPropiaOFallar(request.destinoId());
        List<Favorito> favoritos = favoritoRepository.findByListaId(origen.getId());

        for (Favorito favorito : favoritos) {
            favorito.actualizar(favorito.getProductoId(), favorito.getNota(), destino.getId());
            favoritoRepository.save(favorito);
        }
        listaRepository.deleteById(origen.getId());
    }

    private Lista buscarPropiaOFallar(Long id) {
        Lista lista = listaRepository.findById(id).orElseThrow(() -> new ListaNotFoundException(id));
        if (!contextoSeguridad.esAdmin() && !contextoSeguridad.usuarioActualId().equals(lista.getUsuarioId())) {
            throw new AccesoDenegadoException();
        }
        return lista;
    }
}
