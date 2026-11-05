package ar.edu.unvime.api_blank.auth.dto;

public record LoginResponse(String token, String tipo, long expiraEnMinutos) {
}
