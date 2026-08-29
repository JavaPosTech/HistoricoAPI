package br.com.fiap.historicoapi.repository.paciente;

import br.com.fiap.historicoapi.config.AbstractTest;
import br.com.fiap.historicoapi.model.entity.paciente.Paciente;
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
        Paciente paciente = pacienteRepository.findById(1).orElseThrow();

        Assertions.assertEquals(1, paciente.getId());
        Assertions.assertEquals("PEDRO", paciente.getNome());
        Assertions.assertEquals("ALMEIDA", paciente.getSobrenome());
        Assertions.assertEquals("12345678901", paciente.getCpf());
        Assertions.assertEquals("pedro.almeida@email.com", paciente.getEmail());
    }

    @Test
    void findByIdSituacaoCadastroCarregadaTest() {
        Paciente paciente = pacienteRepository.findById(1).orElseThrow();

        Assertions.assertNotNull(paciente.getSituacaoCadastro());
        Assertions.assertEquals("ATIVO", paciente.getSituacaoCadastro().getDescricao());
    }

    @Test
    void findByIdInexistenteTest() {
        Assertions.assertTrue(pacienteRepository.findById(Integer.MAX_VALUE).isEmpty());
    }
}
