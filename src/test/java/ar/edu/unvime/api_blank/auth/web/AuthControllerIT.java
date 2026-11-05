package ar.edu.unvime.api_blank.auth.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import ar.edu.unvime.api_blank.IntegracionBase;

class AuthControllerIT extends IntegracionBase {

    @Test
    void login_credencialesValidas_devuelve200ConToken() throws Exception {
        // Preparar
        String email = emailUnico();
        registrar(email, PASSWORD);

        // Actuar / Verificar
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void login_credencialesInvalidas_devuelve401() throws Exception {
        // Preparar
        String email = emailUnico();
        registrar(email, PASSWORD);

        // Actuar / Verificar
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"incorrecta\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void registro_emailDuplicado_devuelve409() throws Exception {
        // Preparar
        String email = emailUnico();
        registrar(email, PASSWORD);

        // Actuar / Verificar
        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    void registro_conRolAdminEnBody_quedaComoUser() throws Exception {
        // Preparar
        String email = emailUnico();

        // Actuar / Verificar
        var result = mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\",\"rol\":\"ADMIN\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.rol").value("USER"))
            .andReturn();
        assertThat(result.getResponse().getContentAsString()).doesNotContain("passwordHash");
    }
}
