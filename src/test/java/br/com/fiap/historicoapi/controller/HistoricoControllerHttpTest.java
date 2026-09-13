package br.com.fiap.historicoapi.controller;

import br.com.fiap.historicoapi.config.AbstractHttpControllerTest;
import br.com.fiap.historicoapi.exceptions.dto.ErrorResponseDTO;
import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HistoricoControllerHttpTest extends AbstractHttpControllerTest {

    private static final Integer PACIENTE_ID = 1;
    private static final String CORPO_ILEGIVEL = "O corpo da requisição não é um JSON válido ou não segue o formato de uma requisição GraphQL!";

    @Test
    void getHistoricoPacienteTest() {
        PacienteDTO paciente = executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", PACIENTE_ID)
                .path("getHistoricoPaciente")
                .entity(PacienteDTO.class)
                .get();

        Assertions.assertNotNull(paciente);
        Assertions.assertEquals(PACIENTE_ID, paciente.id());
        Assertions.assertEquals("PEDRO", paciente.nome());
        Assertions.assertEquals("ATIVO", paciente.situacaoCadastro());
        Assertions.assertFalse(paciente.historico().isEmpty());
        Assertions.assertFalse(paciente.consultas().isEmpty());
    }

    @Test
    void getHistoricoPacienteInexistenteTest() {
        executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", Integer.MAX_VALUE)
                .errors()
                .satisfy(erros -> {
                    Assertions.assertEquals(1, erros.size());
                    Assertions.assertEquals(ErrorType.NOT_FOUND, erros.getFirst().getErrorType());
                });
    }

    @Test
    void graphqlCorpoSemQueryTest() {
        ResponseEntity<ErrorResponseDTO> resposta = enviarRequisicao(HttpMethod.POST, "{\"variables\":{\"pacienteId\":1}}");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        Assertions.assertEquals("/HistoricoAPI/problems/invalid-graphql-request", resposta.getBody().type().toString());
        Assertions.assertEquals("O corpo da requisição não é uma requisição GraphQL válida!", resposta.getBody().detail());
    }

    @Test
    void graphqlJsonMalformadoTest() {
        ResponseEntity<ErrorResponseDTO> resposta = enviarRequisicao(HttpMethod.POST, "{\"query\": ");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        Assertions.assertEquals("/HistoricoAPI/problems/unreadable-message", resposta.getBody().type().toString());
        Assertions.assertEquals(CORPO_ILEGIVEL, resposta.getBody().detail());
    }

    @Test
    void graphqlPropriedadeDesconhecidaTest() {
        ResponseEntity<ErrorResponseDTO> resposta = enviarRequisicao(HttpMethod.POST, "{\"query\":\"{ __typename }\",\"foo\":1}");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        Assertions.assertEquals(CORPO_ILEGIVEL, resposta.getBody().detail());
    }

    @Test
    void graphqlMetodoNaoPermitidoTest() {
        ResponseEntity<ErrorResponseDTO> resposta = enviarRequisicao(HttpMethod.PUT, "{\"query\":\"{ __typename }\"}");

        Assertions.assertEquals(HttpStatus.METHOD_NOT_ALLOWED, resposta.getStatusCode());
        Assertions.assertEquals(Set.of(HttpMethod.POST), resposta.getHeaders().getAllow());
        Assertions.assertEquals("/HistoricoAPI/problems/method-not-allowed", resposta.getBody().type().toString());
    }
}
