package ar.edu.unvime.api_blank.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ar.edu.unvime.api_blank.usuario.domain.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long vigenciaMinutos;

    public JwtService(
        @Value("${app.jwt.secreto}") String secreto,
        @Value("${app.jwt.vigencia-minutos}") long vigenciaMinutos
    ) {
        byte[] secretBytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secreto debe tener al menos 32 bytes para HS256");
        }
        if (vigenciaMinutos <= 0) {
            throw new IllegalStateException("app.jwt.vigencia-minutos debe ser mayor que cero");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.vigenciaMinutos = vigenciaMinutos;
    }

    public String generarToken(Long usuarioId, Rol rol) {
        Instant ahora = Instant.now();
        return Jwts.builder()
            .subject(usuarioId.toString())
            .claim("rol", rol.name())
            .issuedAt(Date.from(ahora))
            .expiration(Date.from(ahora.plus(vigenciaMinutos, ChronoUnit.MINUTES)))
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
