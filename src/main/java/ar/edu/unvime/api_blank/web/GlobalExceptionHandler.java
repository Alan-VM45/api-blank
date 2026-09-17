package ar.edu.unvime.api_blank.web;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import ar.edu.unvime.api_blank.favorito.exception.FavoritoNotFoundException;
import ar.edu.unvime.api_blank.producto.exception.ExternalServiceException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FavoritoNotFoundException.class)
    public ResponseEntity<ApiError> favoritoNotFound(FavoritoNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
        List<Map<String, String>> details = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> Map.of("campo", error.getField(), "mensaje", error.getDefaultMessage()))
            .toList();
        return error(HttpStatus.BAD_REQUEST, "La solicitud contiene datos invalidos", details);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiError> externalService(ExternalServiceException exception) {
        return error(HttpStatus.BAD_GATEWAY, exception.getMessage(), null);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> restClient(RestClientException exception) {
        return error(HttpStatus.BAD_GATEWAY, "No se pudo consultar el servicio externo", null);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, Object details) {
        return ResponseEntity.status(status).body(new ApiError(
            Instant.now(), status.value(), status.getReasonPhrase(), message, details
        ));
    }

    public record ApiError(Instant timestamp, int status, String error, String message, Object details) {
    }
}
