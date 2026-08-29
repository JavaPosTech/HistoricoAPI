package br.com.fiap.historicoapi.repository.historicopaciente;

import br.com.fiap.historicoapi.config.AbstractTest;
import br.com.fiap.historicoapi.model.entity.historicopaciente.HistoricoPaciente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HistoricoPacienteRepositoryTest extends AbstractTest {

    @Autowired
    private HistoricoPacienteRepository historicoPacienteRepository;

    @Test
    void findByPacienteIdTest() {
        List<HistoricoPaciente> historico = historicoPacienteRepository.findByPacienteId(1);

        Assertions.assertFalse(historico.isEmpty());
        Assertions.assertTrue(historico.stream()
                .map(HistoricoPaciente::getQueixaPrincipal)
                .anyMatch("Dor no peito"::equals));
        Assertions.assertTrue(historico.stream()
                .allMatch(registro -> registro.getPaciente().getId().equals(1)));
    }

    @Test
    void findByPacienteIdInexistenteTest() {
        Assertions.assertTrue(historicoPacienteRepository.findByPacienteId(Integer.MAX_VALUE).isEmpty());
    }
}
