package ar.edu.unvime.api_blank.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.unvime.api_blank.auth.dto.LoginRequest;
import ar.edu.unvime.api_blank.auth.exception.CredencialesInvalidasException;
import ar.edu.unvime.api_blank.security.JwtService;
import ar.edu.unvime.api_blank.usuario.domain.Rol;
import ar.edu.unvime.api_blank.usuario.domain.Usuario;
import ar.edu.unvime.api_blank.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    private AuthServiceImpl service;

    @BeforeEach
    void prepararServicio() {
        service = new AuthServiceImpl(usuarioRepository, passwordEncoder, jwtService, 60);
    }

    @Test
    void login_credencialesValidas_devuelveToken() {
        // Preparar
        when(usuarioRepository.findByEmail("ana@example.com"))
            .thenReturn(Optional.of(new Usuario(9L, "ana@example.com", "hash", Rol.USER)));
        when(passwordEncoder.matches("password-segura", "hash")).thenReturn(true);
        when(jwtService.generarToken(9L, Rol.USER)).thenReturn("jwt-test");

        // Actuar
        var response = service.login(new LoginRequest("ana@example.com", "password-segura"));

        // Verificar
        assertThat(response.token()).isEqualTo("jwt-test");
        assertThat(response.tipo()).isEqualTo("Bearer");
    }

    @Test
    void login_passwordIncorrecta_lanzaCredencialesInvalidas() {
        // Preparar
        when(usuarioRepository.findByEmail("ana@example.com"))
            .thenReturn(Optional.of(new Usuario(9L, "ana@example.com", "hash", Rol.USER)));
        when(passwordEncoder.matches("incorrecta", "hash")).thenReturn(false);

        // Actuar / Verificar
        assertThatThrownBy(() -> service.login(new LoginRequest("ana@example.com", "incorrecta")))
            .isInstanceOf(CredencialesInvalidasException.class)
            .hasMessage("Credenciales inválidas");
    }

    @Test
    void login_emailInexistente_lanzaCredencialesInvalidasConMismoMensaje() {
        // Preparar
        when(usuarioRepository.findByEmail("nadie@example.com")).thenReturn(Optional.empty());

        // Actuar / Verificar
        assertThatThrownBy(() -> service.login(new LoginRequest("nadie@example.com", "incorrecta")))
            .isInstanceOf(CredencialesInvalidasException.class)
            .hasMessage("Credenciales inválidas");
    }
}
