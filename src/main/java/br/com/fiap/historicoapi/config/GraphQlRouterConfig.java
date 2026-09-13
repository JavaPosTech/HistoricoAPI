package br.com.fiap.historicoapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

@Configuration
public class GraphQlRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> metodosNaoPermitidosGraphQl(@Value("${spring.graphql.http.path:/graphql}") String caminhoGraphQl) {
        return RouterFunctions.route(
                RequestPredicates.path(caminhoGraphQl).and(RequestPredicates.methods(HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE)),
                requisicao -> {
                    throw new HttpRequestMethodNotSupportedException(requisicao.method().name(), List.of(HttpMethod.POST.name()));
                });
    }
}