package ar.edu.unvime.api_blank.usuario.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.api_blank.usuario.domain.Usuario;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = usuario.getId() == null
            ? new UsuarioEntity()
            : usuarioJpaRepository.findById(usuario.getId()).orElse(new UsuarioEntity());
        entity.setEmail(usuario.getEmail());
        entity.setPasswordHash(usuario.getPasswordHash());
        entity.setRol(usuario.getRol());
        if (usuario.getId() != null) {
            entity.setId(usuario.getId());
        }
        return toDomain(usuarioJpaRepository.save(entity));
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioJpaRepository.findById(id).map(UsuarioRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioJpaRepository.findByEmail(email).map(UsuarioRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioJpaRepository.existsByEmail(email);
    }

    private static Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getRol());
    }
}
