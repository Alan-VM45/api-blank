package ar.edu.unvime.api_blank.lista.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.unvime.api_blank.lista.domain.Lista;

public interface ListaRepository {
    Lista save(Lista lista);
    List<Lista> findAll();
    Optional<Lista> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
}
