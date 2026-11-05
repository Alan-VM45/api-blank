package ar.edu.unvime.api_blank.auth.service;

import ar.edu.unvime.api_blank.auth.dto.LoginRequest;
import ar.edu.unvime.api_blank.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
