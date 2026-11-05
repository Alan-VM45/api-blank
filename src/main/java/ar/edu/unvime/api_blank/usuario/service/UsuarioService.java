package ar.edu.unvime.api_blank.usuario.service;

import ar.edu.unvime.api_blank.auth.dto.RegistroRequest;
import ar.edu.unvime.api_blank.auth.dto.UsuarioResponse;

public interface UsuarioService {
    UsuarioResponse registrar(RegistroRequest request);
}
