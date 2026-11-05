package ar.edu.unvime.api_blank.auth.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.edu.unvime.api_blank.auth.dto.LoginRequest;
import ar.edu.unvime.api_blank.auth.dto.LoginResponse;
import ar.edu.unvime.api_blank.auth.exception.CredencialesInvalidasException;
import ar.edu.unvime.api_blank.security.JwtService;
import ar.edu.unvime.api_blank.usuario.domain.Usuario;
import ar.edu.unvime.api_blank.usuario.repository.UsuarioRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long vigenciaMinutos;

    public AuthServiceImpl(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        @Value("${app.jwt.vigencia-minutos}") long vigenciaMinutos
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.vigenciaMinutos = vigenciaMinutos;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email().trim().toLowerCase(Locale.ROOT))
            .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }
        return new LoginResponse(jwtService.generarToken(usuario.getId(), usuario.getRol()), "Bearer", vigenciaMinutos);
    }
}
