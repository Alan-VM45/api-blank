package ar.edu.unvime.api_blank.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

    @Test
    void health_siempre_devuelveStatusOk() {
        // Preparar
        HealthController controller = new HealthController();

        // Actuar
        var response = controller.health();

        // Verificar
        assertThat(response).containsEntry("status", "ok").containsEntry("service", "api-blank");
    }
}
