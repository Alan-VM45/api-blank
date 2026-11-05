package ar.edu.unvime.api_blank.usuario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ar.edu.unvime.api_blank.auth.dto.RegistroRequest;
import ar.edu.unvime.api_blank.usuario.domain.Rol;
import ar.edu.unvime.api_blank.usuario.domain.Usuario;
import ar.edu.unvime.api_blank.usuario.exception.EmailYaRegistradoException;
import ar.edu.unvime.api_blank.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UsuarioServiceImpl service;

    @BeforeEach
    void prepararServicio() {
        service = new UsuarioServiceImpl(usuarioRepository, passwordEncoder);
    }

    @Test
    void registrar_passwordValida_guardaHashYNoTextoPlano() {
        // Preparar
        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Actuar
        service.registrar(new RegistroRequest(" Ana@Example.com ", "password-segura"));

        // Verificar
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        String hash = captor.getValue().getPasswordHash();
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@example.com");
        assertThat(hash).isNotEqualTo("password-segura").startsWith("$2");
        assertThat(passwordEncoder.matches("password-segura", hash)).isTrue();
    }

    @Test
    void registrar_emailDuplicado_lanzaEmailYaRegistrado() {
        // Preparar
        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(true);

        // Actuar / Verificar
        assertThatThrownBy(() -> service.registrar(new RegistroRequest("ANA@example.com", "password-segura")))
            .isInstanceOf(EmailYaRegistradoException.class);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_cualquierUsuario_asignaRolUser() {
        // Preparar
        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Actuar
        var response = service.registrar(new RegistroRequest("ana@example.com", "password-segura"));

        // Verificar
        assertThat(response.rol()).isEqualTo(Rol.USER);
    }
}
