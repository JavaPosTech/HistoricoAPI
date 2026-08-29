package br.com.fiap.historicoapi.repository.agendamento;

import br.com.fiap.historicoapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AgendamentoRepositoryTest extends AbstractTest {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Test
    void findByPacienteIdTest() {
        var consultas = Assertions.assertDoesNotThrow(() -> agendamentoRepository.findByPacienteId(1));
        Assertions.assertNotNull(consultas);
    }
}