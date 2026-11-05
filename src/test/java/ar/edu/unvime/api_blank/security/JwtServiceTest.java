package ar.edu.unvime.api_blank.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

import ar.edu.unvime.api_blank.usuario.domain.Rol;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SECRET = "secreto-de-prueba-con-32-bytes-minimo";

    @Test
    void validar_tokenValido_devuelveIdYRol() {
        // Preparar
        JwtService service = new JwtService(SECRET, 60);
        String token = service.generarToken(42L, Rol.ADMIN);

        // Actuar
        var claims = service.validarYObtenerClaims(token);

        // Verificar
        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("rol", String.class)).isEqualTo("ADMIN");
    }

    @Test
    void validar_tokenVencido_lanzaExcepcion() {
        // Preparar
        JwtService service = new JwtService(SECRET, 60);
        Instant ahora = Instant.now();
        String token = Jwts.builder()
            .subject("42")
            .claim("rol", "USER")
            .issuedAt(Date.from(ahora.minusSeconds(120)))
            .expiration(Date.from(ahora.minusSeconds(60)))
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
            .compact();

        // Actuar / Verificar
        assertThatThrownBy(() -> service.validarYObtenerClaims(token)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void validar_tokenDeOtroSecreto_lanzaExcepcion() {
        // Preparar
        JwtService issuer = new JwtService(SECRET, 60);
        JwtService verifier = new JwtService("otro-secreto-de-prueba-con-32-bytes", 60);
        String token = issuer.generarToken(42L, Rol.USER);

        // Actuar / Verificar
        assertThatThrownBy(() -> verifier.validarYObtenerClaims(token)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void validar_tokenAlterado_lanzaExcepcion() {
        // Preparar
        JwtService service = new JwtService(SECRET, 60);
        String token = service.generarToken(42L, Rol.USER);
        String alterado = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        // Actuar / Verificar
        assertThatThrownBy(() -> service.validarYObtenerClaims(alterado)).isInstanceOf(RuntimeException.class);
    }
}
