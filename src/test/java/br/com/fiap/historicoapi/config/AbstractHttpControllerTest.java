package br.com.fiap.historicoapi.config;

import br.com.fiap.historicoapi.exceptions.dto.ErrorResponseDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

@ActiveProfiles("test")
@AutoConfigureHttpGraphQlTester
@Import(value = TestDataBaseConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractHttpControllerTest {

    @Value("${local.server.port}")
    private int porta;

    @Autowired
    protected HttpGraphQlTester graphQlTester;

    protected GraphQlTester.Response executarQuery(String documento, String nomeVariavel, Object valor) {
        return graphQlTester.document(documento)
                .variable(nomeVariavel, valor)
                .execute();
    }

    protected ResponseEntity<ErrorResponseDTO> enviarRequisicao(HttpMethod metodo, String corpo) {
        return RestClient.create("http://localhost:" + porta + "/HistoricoAPI/graphql")
                .method(metodo)
                .contentType(MediaType.APPLICATION_JSON)
                .body(corpo)
                .exchange((requisicao, resposta) -> ResponseEntity.status(resposta.getStatusCode())
                        .headers(resposta.getHeaders())
                        .body(resposta.bodyTo(ErrorResponseDTO.class)));
    }
}
