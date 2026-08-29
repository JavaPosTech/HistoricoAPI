package br.com.fiap.historicoapi.repository.agendamento;

import br.com.fiap.historicoapi.config.AbstractTest;
import br.com.fiap.historicoapi.model.entity.agendamento.Agendamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class AgendamentoRepositoryTest extends AbstractTest {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Test
    void findByPacienteIdTest() {
        List<Agendamento> consultas = agendamentoRepository.findByPacienteId(1);

        Assertions.assertFalse(consultas.isEmpty());
        Assertions.assertTrue(consultas.stream()
                .allMatch(consulta -> consulta.getPaciente().getId().equals(1)));
    }

    @Test
    void findByPacienteIdMedicoCarregadoTest() {
        List<Agendamento> consultas = agendamentoRepository.findByPacienteId(1);

        Assertions.assertFalse(consultas.isEmpty());
        Assertions.assertTrue(consultas.stream()
                .allMatch(consulta -> consulta.getMedico() != null && !consulta.getMedico().getNome().isBlank()));
    }

    @Test
    void findByPacienteIdInexistenteTest() {
        Assertions.assertTrue(agendamentoRepository.findByPacienteId(Integer.MAX_VALUE).isEmpty());
    }
}
