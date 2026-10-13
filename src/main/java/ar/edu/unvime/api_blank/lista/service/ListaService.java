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

@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final FavoritoRepository favoritoRepository;

    public ListaService(ListaRepository listaRepository, FavoritoRepository favoritoRepository) {
        this.listaRepository = listaRepository;
        this.favoritoRepository = favoritoRepository;
    }

    public ListaResponse crear(ListaRequest request) {
        Lista lista = new Lista(null, request.nombre());
        return ListaResponse.from(listaRepository.save(lista));
    }

    public List<ListaResponse> listar() {
        return listaRepository.findAll().stream().map(ListaResponse::from).toList();
    }

    public ListaResponse obtener(Long id) {
        return ListaResponse.from(findOrThrow(id));
    }

    public List<Favorito> favoritosDeLista(Long listaId) {
        findOrThrow(listaId);
        return favoritoRepository.findByListaId(listaId);
    }

    public void eliminar(Long id) {
        Lista lista = findOrThrow(id);
        if (!favoritoRepository.findByListaId(lista.getId()).isEmpty()) {
            throw new ListaConFavoritosException(id);
        }
        listaRepository.deleteById(id);
    }

    @Transactional
    public void moverFavoritos(Long origenId, MoverFavoritosRequest request) {
        if (request.destinoId() == null) {
            throw new IllegalArgumentException("destinoId es obligatorio");
        }
        if (origenId.equals(request.destinoId())) {
            throw new IllegalArgumentException("La lista origen y destino deben ser distintas");
        }

        Lista origen = findOrThrow(origenId);
        Lista destino = findOrThrow(request.destinoId());

        List<Favorito> favoritos = favoritoRepository.findByListaId(origen.getId());

        for (Favorito favorito : favoritos) {
            favorito.actualizar(favorito.getProductoId(), favorito.getNota(), destino.getId());
            favoritoRepository.save(favorito);
        }

        listaRepository.deleteById(origen.getId());
    }

    private Lista findOrThrow(Long id) {
        return listaRepository.findById(id).orElseThrow(() -> new ListaNotFoundException(id));
    }
}
