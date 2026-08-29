package br.com.fiap.historicoapi.repository.historicopaciente;

import br.com.fiap.historicoapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HistoricoPacienteRepositoryTest extends AbstractTest {

    @Autowired
    private HistoricoPacienteRepository historicoPacienteRepository;

    @Test
    void findByPacienteIdTest() {
        var historico = Assertions.assertDoesNotThrow(() -> historicoPacienteRepository.findByPacienteId(1));
        Assertions.assertNotNull(historico);
    }
}