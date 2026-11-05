package ar.edu.unvime.api_blank.lista.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.api_blank.lista.domain.Lista;
import ar.edu.unvime.api_blank.usuario.repository.UsuarioJpaRepository;
import ar.edu.unvime.api_blank.usuario.repository.UsuarioEntity;

@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository listaJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository listaJpaRepository, UsuarioJpaRepository usuarioJpaRepository) {
        this.listaJpaRepository = listaJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Lista save(Lista lista) {
        ListaEntity entity = lista.getId() == null
            ? new ListaEntity()
            : listaJpaRepository.findById(lista.getId()).orElse(new ListaEntity());

        entity.setNombre(lista.getNombre());
        entity.setUsuario(lista.getUsuarioId() == null
            ? null
            : usuarioJpaRepository.getReferenceById(lista.getUsuarioId()));
        if (lista.getId() != null) {
            entity.setId(lista.getId());
        }

        return toDomain(listaJpaRepository.save(entity));
    }

    @Override
    public List<Lista> findAll() {
        return listaJpaRepository.findAll().stream().map(ListaRepositoryAdapter::toDomain).toList();
    }

    @Override
    public List<Lista> findByUsuarioId(Long usuarioId) {
        return listaJpaRepository.findByUsuario_Id(usuarioId).stream()
            .map(ListaRepositoryAdapter::toDomain)
            .toList();
    }

    @Override
    public Optional<Lista> findById(Long id) {
        return listaJpaRepository.findById(id).map(ListaRepositoryAdapter::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        listaJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return listaJpaRepository.existsById(id);
    }

    private static Lista toDomain(ListaEntity entity) {
        UsuarioEntity usuario = entity.getUsuario();
        return new Lista(entity.getId(), entity.getNombre(), usuario == null ? null : usuario.getId());
    }
}
