package ar.edu.unvime.api_blank.lista.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.unvime.api_blank.favorito.repository.FavoritoRepository;
import ar.edu.unvime.api_blank.lista.domain.Lista;
import ar.edu.unvime.api_blank.lista.dto.ListaRequest;
import ar.edu.unvime.api_blank.lista.dto.MoverFavoritosRequest;
import ar.edu.unvime.api_blank.lista.exception.ListaNotFoundException;
import ar.edu.unvime.api_blank.lista.repository.ListaRepository;
import ar.edu.unvime.api_blank.security.AccesoDenegadoException;
import ar.edu.unvime.api_blank.security.ContextoSeguridad;

@ExtendWith(MockitoExtension.class)
class ListaServiceImplTest {

    @Mock
    private ListaRepository listaRepository;
    @Mock
    private FavoritoRepository favoritoRepository;
    @Mock
    private ContextoSeguridad contextoSeguridad;
    @InjectMocks
    private ListaServiceImpl service;

    @Test
    void crear_usuarioAutenticado_asignaDuenoDelContexto() {
        // Preparar
        when(contextoSeguridad.usuarioActualId()).thenReturn(17L);
        when(listaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Actuar
        service.crear(new ListaRequest("Lecturas"));

        // Verificar
        ArgumentCaptor<Lista> captor = ArgumentCaptor.forClass(Lista.class);
        verify(listaRepository).save(captor.capture());
        assertThat(captor.getValue().getUsuarioId()).isEqualTo(17L);
    }

    @Test
    void obtener_listaPropia_devuelveLista() {
        // Preparar
        when(listaRepository.findById(3L)).thenReturn(Optional.of(new Lista(3L, "Propia", 17L)));
        when(contextoSeguridad.usuarioActualId()).thenReturn(17L);
        when(contextoSeguridad.esAdmin()).thenReturn(false);

        // Actuar
        var response = service.obtener(3L);

        // Verificar
        assertThat(response.nombre()).isEqualTo("Propia");
        assertThat(response.usuarioId()).isEqualTo(17L);
    }

    @Test
    void obtener_listaAjena_lanzaAccesoDenegado() {
        // Preparar
        when(listaRepository.findById(3L)).thenReturn(Optional.of(new Lista(3L, "Ajena", 22L)));
        when(contextoSeguridad.usuarioActualId()).thenReturn(17L);
        when(contextoSeguridad.esAdmin()).thenReturn(false);

        // Actuar / Verificar
        assertThatThrownBy(() -> service.obtener(3L)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void obtener_listaAjenaComoAdmin_devuelveLista() {
        // Preparar
        when(listaRepository.findById(3L)).thenReturn(Optional.of(new Lista(3L, "Ajena", 22L)));
        when(contextoSeguridad.esAdmin()).thenReturn(true);

        // Actuar
        var response = service.obtener(3L);

        // Verificar
        assertThat(response.usuarioId()).isEqualTo(22L);
    }

    @Test
    void obtener_listaInexistente_lanzaListaNotFound() {
        // Preparar
        when(listaRepository.findById(3L)).thenReturn(Optional.empty());

        // Actuar / Verificar
        assertThatThrownBy(() -> service.obtener(3L)).isInstanceOf(ListaNotFoundException.class);
    }

    @Test
    void listar_comoUser_devuelveSoloPropias() {
        // Preparar
        when(contextoSeguridad.esAdmin()).thenReturn(false);
        when(contextoSeguridad.usuarioActualId()).thenReturn(17L);
        when(listaRepository.findByUsuarioId(17L)).thenReturn(List.of(new Lista(1L, "Propia", 17L)));

        // Actuar
        var response = service.listar();

        // Verificar
        assertThat(response).extracting("nombre").containsExactly("Propia");
        verify(listaRepository).findByUsuarioId(17L);
    }

    @Test
    void listar_comoAdmin_devuelveTodas() {
        // Preparar
        when(contextoSeguridad.esAdmin()).thenReturn(true);
        when(listaRepository.findAll()).thenReturn(List.of(
            new Lista(1L, "Primera", 17L),
            new Lista(2L, "Sin clasificar", null)
        ));

        // Actuar
        var response = service.listar();

        // Verificar
        assertThat(response).hasSize(2);
        verify(listaRepository).findAll();
    }

    @Test
    void moverFavoritos_destinoAjeno_lanzaAccesoDenegado() {
        // Preparar
        when(listaRepository.findById(1L)).thenReturn(Optional.of(new Lista(1L, "Propia", 17L)));
        when(listaRepository.findById(2L)).thenReturn(Optional.of(new Lista(2L, "Ajena", 22L)));
        when(contextoSeguridad.usuarioActualId()).thenReturn(17L);
        when(contextoSeguridad.esAdmin()).thenReturn(false);

        // Actuar / Verificar
        assertThatThrownBy(() -> service.moverFavoritos(1L, new MoverFavoritosRequest(2L)))
            .isInstanceOf(AccesoDenegadoException.class);
    }
}
