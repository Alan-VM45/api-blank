package ar.edu.unvime.api_blank.usuario.repository;

import java.util.Optional;

import ar.edu.unvime.api_blank.usuario.domain.Usuario;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
