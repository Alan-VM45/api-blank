package ar.edu.unvime.api_blank.favorito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Long> {
    List<FavoritoEntity> findByListaId(Long listaId);
    List<FavoritoEntity> findByLista_Id(Long listaId);
}
