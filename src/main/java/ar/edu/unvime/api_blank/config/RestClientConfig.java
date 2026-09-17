package ar.edu.unvime.api_blank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    RestClient dummyJsonClient(RestClient.Builder builder) {
        return builder.baseUrl("https://dummyjson.com").build();
    }
}
