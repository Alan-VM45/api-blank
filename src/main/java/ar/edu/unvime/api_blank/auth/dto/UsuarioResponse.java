package ar.edu.unvime.api_blank.auth.dto;

import ar.edu.unvime.api_blank.usuario.domain.Rol;
import ar.edu.unvime.api_blank.usuario.domain.Usuario;

public record UsuarioResponse(Long id, String email, Rol rol) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getEmail(), usuario.getRol());
    }
}
