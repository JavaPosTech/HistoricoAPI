package br.com.fiap.historicoapi.controller;

import br.com.fiap.historicoapi.config.AbstractControllerTest;
import br.com.fiap.historicoapi.model.dto.agendamento.AgendamentoDTO;
import br.com.fiap.historicoapi.model.dto.historicopaciente.HistoricoPacienteDTO;
import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;

@SpringBootTest
class HistoricoControllerTest extends AbstractControllerTest {

    private static final Integer PACIENTE_ID = 1;
    private static final String PADRAO_DATA_HORA = "[0-9]{2}/[0-9]{2}/[0-9]{4} - [0-9]{2}:[0-9]{2}:[0-9]{2}";

    @Test
    void getHistoricoPacienteTest() {
        PacienteDTO paciente = executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", PACIENTE_ID)
                .path("getHistoricoPaciente")
                .entity(PacienteDTO.class)
                .get();

        Assertions.assertNotNull(paciente);
        Assertions.assertEquals(PACIENTE_ID, paciente.id());
        Assertions.assertEquals("PEDRO", paciente.nome());
        Assertions.assertEquals("ALMEIDA", paciente.sobrenome());
        Assertions.assertEquals("12345678901", paciente.cpf());
        Assertions.assertEquals("pedro.almeida@email.com", paciente.email());
        Assertions.assertEquals("15/05/1990", paciente.dataNascimento());
        Assertions.assertEquals("ATIVO", paciente.situacaoCadastro());
        Assertions.assertTrue(paciente.dataCadastro().matches(PADRAO_DATA_HORA));
    }

    @Test
    void getHistoricoPacienteHistoricoClinicoTest() {
        PacienteDTO paciente = executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", PACIENTE_ID)
                .path("getHistoricoPaciente")
                .entity(PacienteDTO.class)
                .get();

        Assertions.assertFalse(paciente.historico().isEmpty());
        Assertions.assertTrue(paciente.historico().stream()
                .map(HistoricoPacienteDTO::queixaPrincipal)
                .anyMatch("Dor no peito"::equals));

        for (HistoricoPacienteDTO historico : paciente.historico()) {
            Assertions.assertNotNull(historico.id());
            Assertions.assertNotNull(historico.historicoDoenca());
            Assertions.assertNotNull(historico.medicamentos());
            Assertions.assertNotNull(historico.alergias());
        }
    }

    @Test
    void getHistoricoPacienteConsultasTest() {
        PacienteDTO paciente = executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", PACIENTE_ID)
                .path("getHistoricoPaciente")
                .entity(PacienteDTO.class)
                .get();

        Assertions.assertFalse(paciente.consultas().isEmpty());

        for (AgendamentoDTO consulta : paciente.consultas()) {
            Assertions.assertNotNull(consulta.id());
            Assertions.assertFalse(consulta.nomeMedico().isBlank());
            Assertions.assertTrue(consulta.dataHoraConsulta().matches(PADRAO_DATA_HORA));
            Assertions.assertTrue(consulta.dataCadastro().matches(PADRAO_DATA_HORA));
        }
    }

    @Test
    void getHistoricoPacienteInexistenteTest() {
        executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", Integer.MAX_VALUE)
                .errors()
                .satisfy(erros -> {
                    Assertions.assertEquals(1, erros.size());
                    Assertions.assertEquals(ErrorType.NOT_FOUND, erros.getFirst().getErrorType());
                    Assertions.assertEquals("Paciente não encontrado - ID: " + Integer.MAX_VALUE, erros.getFirst().getMessage());
                });
    }

    @Test
    void getHistoricoPacienteIdZeroTest() {
        executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", 0)
                .errors()
                .satisfy(erros -> {
                    Assertions.assertEquals(1, erros.size());
                    Assertions.assertEquals(ErrorType.BAD_REQUEST, erros.getFirst().getErrorType());
                    Assertions.assertEquals("O ID do Paciente deve ser um número inteiro positivo!", erros.getFirst().getMessage());
                });
    }

    @Test
    void getHistoricoPacienteIdNegativoTest() {
        executarQuery(QueryGraphQl.HISTORICO_PACIENTE, "pacienteId", -1)
                .errors()
                .satisfy(erros -> {
                    Assertions.assertEquals(1, erros.size());
                    Assertions.assertEquals(ErrorType.BAD_REQUEST, erros.getFirst().getErrorType());
                });
    }
}
