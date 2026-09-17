package ar.edu.unvime.api_blank.producto.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import ar.edu.unvime.api_blank.producto.dto.DummyJsonProduct;
import ar.edu.unvime.api_blank.producto.dto.DummyJsonProductsResponse;
import ar.edu.unvime.api_blank.producto.dto.ProductoResponse;
import ar.edu.unvime.api_blank.producto.exception.ExternalServiceException;

@Service
public class ProductoService {

    private final RestClient dummyJsonClient;

    public ProductoService(RestClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    public List<ProductoResponse> listar(Integer limit, Integer skip) {
        try {
            DummyJsonProductsResponse response = dummyJsonClient.get()
                .uri(uriBuilder -> uriBuilder.path("/products")
                    .queryParam("limit", limit)
                    .queryParam("skip", skip)
                    .build())
                .retrieve()
                .body(DummyJsonProductsResponse.class);

            return response == null || response.products() == null
                ? List.of()
                : response.products().stream().map(this::toResponse).toList();
        } catch (RestClientException exception) {
            throw new ExternalServiceException("No se pudo consultar el catalogo de productos", exception);
        }
    }

    public ProductoResponse obtener(Long id) {
        try {
            DummyJsonProduct product = dummyJsonClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .body(DummyJsonProduct.class);
            return toResponse(product);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("No se pudo consultar el producto " + id, exception);
        }
    }

    private ProductoResponse toResponse(DummyJsonProduct product) {
        return new ProductoResponse(
            product.id(),
            product.title(),
            product.description(),
            product.price(),
            product.category(),
            product.thumbnail()
        );
    }
}
