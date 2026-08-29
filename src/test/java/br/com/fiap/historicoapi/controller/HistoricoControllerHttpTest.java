package br.com.fiap.historicoapi.controller;

import br.com.fiap.historicoapi.config.AbstractHttpControllerTest;
import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HistoricoControllerHttpTest extends AbstractHttpControllerTest {

    private static final Integer PACIENTE_ID = 1;

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
}
