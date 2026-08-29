package br.com.fiap.historicoapi.service;

import br.com.fiap.historicoapi.config.AbstractTest;
import br.com.fiap.historicoapi.exceptions.PacienteNaoEncontradoException;
import br.com.fiap.historicoapi.exceptions.RequisicaoInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HistoricoServiceTest extends AbstractTest {

    @Autowired
    private HistoricoService historicoService;

    @Test
    void buscarHistoricoPorPacienteIdTest() {
        Assertions.assertDoesNotThrow(() -> historicoService.buscarHistoricoPorPacienteId(1));
    }

    @Test
    void buscarHistoricoPorPacienteIdNullTest() {
        Assertions.assertThrows(RequisicaoInvalidaException.class, () -> historicoService.buscarHistoricoPorPacienteId(null));
    }

    @Test
    void buscarHistoricoPorPacienteIdInexistenteTest() {
        Assertions.assertThrows(PacienteNaoEncontradoException.class, () -> historicoService.buscarHistoricoPorPacienteId(Integer.MAX_VALUE));
    }
}