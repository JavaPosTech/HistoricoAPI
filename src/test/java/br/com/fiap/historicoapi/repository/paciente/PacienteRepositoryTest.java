package br.com.fiap.historicoapi.repository.paciente;

import br.com.fiap.historicoapi.config.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PacienteRepositoryTest extends AbstractTest {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Test
    void findByIdTest() {
        var paciente = Assertions.assertDoesNotThrow(() -> pacienteRepository.findById(1));
        Assertions.assertNotNull(paciente);
    }
}