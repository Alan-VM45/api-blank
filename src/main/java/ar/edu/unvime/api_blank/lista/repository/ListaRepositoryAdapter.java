package ar.edu.unvime.api_blank.lista.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.api_blank.lista.domain.Lista;

@Repository
public class ListaRepositoryAdapter implements ListaRepository {

    private final ListaJpaRepository listaJpaRepository;

    public ListaRepositoryAdapter(ListaJpaRepository listaJpaRepository) {
        this.listaJpaRepository = listaJpaRepository;
    }

    @Override
    public Lista save(Lista lista) {
        ListaEntity entity = lista.getId() == null
            ? new ListaEntity()
            : listaJpaRepository.findById(lista.getId()).orElse(new ListaEntity());

        entity.setNombre(lista.getNombre());
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
        return new Lista(entity.getId(), entity.getNombre());
    }
}
