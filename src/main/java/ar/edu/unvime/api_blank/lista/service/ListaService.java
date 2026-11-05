package ar.edu.unvime.api_blank.lista.service;

import java.util.List;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;
import ar.edu.unvime.api_blank.lista.dto.ListaRequest;
import ar.edu.unvime.api_blank.lista.dto.ListaResponse;
import ar.edu.unvime.api_blank.lista.dto.MoverFavoritosRequest;

public interface ListaService {
    ListaResponse crear(ListaRequest request);
    List<ListaResponse> listar();
    ListaResponse obtener(Long id);
    List<Favorito> favoritosDeLista(Long listaId);
    void eliminar(Long id);
    void moverFavoritos(Long origenId, MoverFavoritosRequest request);
}
