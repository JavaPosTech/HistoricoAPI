package br.com.fiap.historicoapi.service;

import br.com.fiap.historicoapi.config.AbstractTest;
import br.com.fiap.historicoapi.exceptions.PacienteNaoEncontradoException;
import br.com.fiap.historicoapi.exceptions.RequisicaoInvalidaException;
import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HistoricoServiceTest extends AbstractTest {

    private static final String MENSAGEM_ID_INVALIDO = "O ID do Paciente deve ser um número inteiro positivo!";

    @Autowired
    private HistoricoService historicoService;

    @Test
    void buscarHistoricoPorPacienteIdTest() {
        PacienteDTO paciente = historicoService.buscarHistoricoPorPacienteId(1);

        Assertions.assertNotNull(paciente);
        Assertions.assertEquals(1, paciente.id());
        Assertions.assertEquals("PEDRO", paciente.nome());
        Assertions.assertEquals("ATIVO", paciente.situacaoCadastro());
        Assertions.assertEquals("15/05/1990", paciente.dataNascimento());
        Assertions.assertFalse(paciente.historico().isEmpty());
        Assertions.assertFalse(paciente.consultas().isEmpty());
    }

    @Test
    void buscarHistoricoPorPacienteIdNullTest() {
        var excecao = Assertions.assertThrows(RequisicaoInvalidaException.class,
                () -> historicoService.buscarHistoricoPorPacienteId(null));

        Assertions.assertEquals(MENSAGEM_ID_INVALIDO, excecao.getMessage());
    }

    @Test
    void buscarHistoricoPorPacienteIdZeroTest() {
        var excecao = Assertions.assertThrows(RequisicaoInvalidaException.class,
                () -> historicoService.buscarHistoricoPorPacienteId(0));

        Assertions.assertEquals(MENSAGEM_ID_INVALIDO, excecao.getMessage());
    }

    @Test
    void buscarHistoricoPorPacienteIdNegativoTest() {
        var excecao = Assertions.assertThrows(RequisicaoInvalidaException.class,
                () -> historicoService.buscarHistoricoPorPacienteId(-1));

        Assertions.assertEquals(MENSAGEM_ID_INVALIDO, excecao.getMessage());
    }

    @Test
    void buscarHistoricoPorPacienteIdInexistenteTest() {
        var excecao = Assertions.assertThrows(PacienteNaoEncontradoException.class,
                () -> historicoService.buscarHistoricoPorPacienteId(Integer.MAX_VALUE));

        Assertions.assertEquals("Paciente não encontrado - ID: " + Integer.MAX_VALUE, excecao.getMessage());
    }
}
