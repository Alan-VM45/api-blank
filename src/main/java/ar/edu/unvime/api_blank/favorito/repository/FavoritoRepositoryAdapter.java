package ar.edu.unvime.api_blank.favorito.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.unvime.api_blank.favorito.domain.Favorito;
import ar.edu.unvime.api_blank.lista.exception.ListaNotFoundException;
import ar.edu.unvime.api_blank.lista.repository.ListaEntity;
import ar.edu.unvime.api_blank.lista.repository.ListaJpaRepository;

@Repository
public class FavoritoRepositoryAdapter implements FavoritoRepository {

    private final FavoritoJpaRepository favoritoJpaRepository;
    private final ListaJpaRepository listaJpaRepository;

    public FavoritoRepositoryAdapter(FavoritoJpaRepository favoritoJpaRepository, ListaJpaRepository listaJpaRepository) {
        this.favoritoJpaRepository = favoritoJpaRepository;
        this.listaJpaRepository = listaJpaRepository;
    }

    @Override
    public Favorito save(Favorito favorito) {
        ListaEntity lista = listaJpaRepository.findById(favorito.getListaId())
            .orElseThrow(() -> new ListaNotFoundException(favorito.getListaId()));

        FavoritoEntity entity = favorito.getId() == null
            ? new FavoritoEntity()
            : favoritoJpaRepository.findById(favorito.getId()).orElse(new FavoritoEntity());

        entity.setProductoId(favorito.getProductoId());
        entity.setNota(favorito.getNota());
        entity.setFechaAlta(favorito.getFechaAgregado());
        entity.setLista(lista);

        if (favorito.getId() != null) {
            entity.setId(favorito.getId());
        }

        return toDomain(favoritoJpaRepository.save(entity));
    }

    @Override
    public List<Favorito> findAll() {
        return favoritoJpaRepository.findAll().stream().map(FavoritoRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<Favorito> findById(Long id) {
        return favoritoJpaRepository.findById(id).map(FavoritoRepositoryAdapter::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        favoritoJpaRepository.deleteById(id);
    }

    @Override
    public List<Favorito> findByListaId(Long listaId) {
        return favoritoJpaRepository.findByListaId(listaId).stream().map(FavoritoRepositoryAdapter::toDomain).toList();
    }

    private static Favorito toDomain(FavoritoEntity entity) {
        return new Favorito(
            entity.getId(),
            entity.getProductoId(),
            entity.getNota(),
            entity.getFechaAlta(),
            entity.getListaId()
        );
    }
}
