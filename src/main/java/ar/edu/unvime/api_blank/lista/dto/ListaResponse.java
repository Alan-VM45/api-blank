package ar.edu.unvime.api_blank.lista.dto;

import ar.edu.unvime.api_blank.lista.domain.Lista;

public record ListaResponse(Long id, String nombre, Long usuarioId) {
    public static ListaResponse from(Lista lista) {
        return new ListaResponse(lista.getId(), lista.getNombre(), lista.getUsuarioId());
    }
}
