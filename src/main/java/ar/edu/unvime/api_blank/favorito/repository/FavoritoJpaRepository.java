package ar.edu.unvime.api_blank.favorito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Long> {
    List<FavoritoEntity> findByListaId(Long listaId);
    List<FavoritoEntity> findByLista_Id(Long listaId);

    @Query("select favorito from FavoritoEntity favorito where favorito.lista.usuario.id = :usuarioId")
    List<FavoritoEntity> findByListaUsuarioId(@Param("usuarioId") Long usuarioId);
}
