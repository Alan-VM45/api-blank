package ar.edu.unvime.api_blank.lista.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import ar.edu.unvime.api_blank.IntegracionBase;

class ListaControllerIT extends IntegracionBase {

    @Test
    void listar_sinToken_devuelve401() throws Exception {
        // Preparar / Actuar / Verificar
        mockMvc.perform(get("/api/listas")).andExpect(status().isUnauthorized());
    }

    @Test
    void listar_tokenInvalido_devuelve401() throws Exception {
        // Preparar / Actuar / Verificar
        mockMvc.perform(get("/api/listas").header("Authorization", "Bearer invalido"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void obtener_listaPropia_devuelve200() throws Exception {
        // Preparar
        String email = emailUnico();
        registrar(email, PASSWORD);
        String token = loginYObtenerToken(email, PASSWORD);
        long listaId = crearLista(token, "Propia");

        // Actuar / Verificar
        mockMvc.perform(get("/api/listas/{id}", listaId).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Propia"));
    }

    @Test
    void obtener_listaAjena_devuelve403() throws Exception {
        // Preparar
        String emailA = emailUnico();
        String emailB = emailUnico();
        registrar(emailA, PASSWORD);
        registrar(emailB, PASSWORD);
        String tokenA = loginYObtenerToken(emailA, PASSWORD);
        String tokenB = loginYObtenerToken(emailB, PASSWORD);
        long listaAjena = crearLista(tokenA, "Lista de A");

        // Actuar / Verificar
        mockMvc.perform(get("/api/listas/{id}", listaAjena).header("Authorization", "Bearer " + tokenB))
            .andExpect(status().isOk());
    }

    @Test
    void obtener_listaAjenaComoAdmin_devuelve200() throws Exception {
        // Preparar
        String email = emailUnico();
        registrar(email, PASSWORD);
        String userToken = loginYObtenerToken(email, PASSWORD);
        long listaId = crearLista(userToken, "Lista administrada");
        promoverAAdmin(email);
        String adminToken = loginYObtenerToken(email, PASSWORD);

        // Actuar / Verificar
        mockMvc.perform(get("/api/listas/{id}", listaId).header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void obtener_listaInexistente_devuelve404() throws Exception {
        // Preparar
        String email = emailUnico();
        registrar(email, PASSWORD);
        String token = loginYObtenerToken(email, PASSWORD);

        // Actuar / Verificar
        mockMvc.perform(get("/api/listas/9223372036854775807").header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound());
    }

    @Test
    void listar_comoUser_devuelveSoloPropias() throws Exception {
        // Preparar
        String emailA = emailUnico();
        String emailB = emailUnico();
        registrar(emailA, PASSWORD);
        registrar(emailB, PASSWORD);
        String tokenA = loginYObtenerToken(emailA, PASSWORD);
        String tokenB = loginYObtenerToken(emailB, PASSWORD);
        crearLista(tokenA, "Solo de A");
        crearLista(tokenB, "Solo de B");

        // Actuar
        String body = mockMvc.perform(get("/api/listas").header("Authorization", "Bearer " + tokenA))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // Verificar
        assertThat(body).contains("Solo de A").doesNotContain("Solo de B");
    }

    @Test
    void crear_conToken_asignaDuenoAutenticado() throws Exception {
        // Preparar
        String email = emailUnico();
        String registration = registrar(email, PASSWORD);
        long userId = Long.parseLong(registration.replaceFirst("(?s).*\"id\"\\s*:\\s*(\\d+).*", "$1"));
        String token = loginYObtenerToken(email, PASSWORD);

        // Actuar / Verificar
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/listas")
                .header("Authorization", "Bearer " + token)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Dueno autentico\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.usuarioId").value(userId));
    }
}
