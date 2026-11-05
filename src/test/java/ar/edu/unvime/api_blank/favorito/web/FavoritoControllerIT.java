package ar.edu.unvime.api_blank.favorito.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import ar.edu.unvime.api_blank.IntegracionBase;

class FavoritoControllerIT extends IntegracionBase {

    @Test
    void crear_enListaAjena_devuelve403() throws Exception {
        // Preparar
        String emailA = emailUnico();
        String emailB = emailUnico();
        registrar(emailA, PASSWORD);
        registrar(emailB, PASSWORD);
        String tokenA = loginYObtenerToken(emailA, PASSWORD);
        String tokenB = loginYObtenerToken(emailB, PASSWORD);
        long listaAjena = crearLista(tokenA, "Lista de destino ajena");

        // Actuar / Verificar
        mockMvc.perform(post("/api/favoritos")
                .header("Authorization", "Bearer " + tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productoId\":1,\"nota\":\"Prueba\",\"listaId\":" + listaAjena + "}"))
            .andExpect(status().isForbidden());
    }
}
