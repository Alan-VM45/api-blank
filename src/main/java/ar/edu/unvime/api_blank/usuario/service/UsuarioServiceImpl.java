package ar.edu.unvime.api_blank.usuario.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unvime.api_blank.auth.dto.RegistroRequest;
import ar.edu.unvime.api_blank.auth.dto.UsuarioResponse;
import ar.edu.unvime.api_blank.usuario.domain.Rol;
import ar.edu.unvime.api_blank.usuario.domain.Usuario;
import ar.edu.unvime.api_blank.usuario.exception.EmailYaRegistradoException;
import ar.edu.unvime.api_blank.usuario.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }

        Usuario usuario = usuarioRepository.save(new Usuario(
            null,
            email,
            passwordEncoder.encode(request.password()),
            Rol.USER
        ));
        return UsuarioResponse.from(usuario);
    }
}
