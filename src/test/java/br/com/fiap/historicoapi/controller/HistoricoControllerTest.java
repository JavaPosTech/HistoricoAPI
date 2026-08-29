package br.com.fiap.historicoapi.controller;

import br.com.fiap.historicoapi.config.AbstractControllerTest;
import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HistoricoControllerTest extends AbstractControllerTest {

    private static final Integer PACIENTE_ID = 1;

    private static final String QUERY_HISTORICO_PACIENTE = """
            query BuscarHistoricoPaciente($pacienteId: ID!) {
                getHistoricoPaciente(pacienteId: $pacienteId) {
                    id
                    nome
                    sobrenome
                    cpf
                    email
                    telefone
                    endereco
                    dataNascimento
                    dataCadastro
                    situacaoCadastro
                    historico {
                        id
                        queixaPrincipal
                        historicoDoenca
                        medicamentos
                        alergias
                        observacoes
                    }
                    consultas {
                        id
                        nomeMedico
                        dataHoraConsulta
                        observacao
                        dataCadastro
                    }
                }
            }
            """;

    @Test
    @Order(1)
    void getHistoricoPacienteTest() {
        PacienteDTO paciente = executarQuery(QUERY_HISTORICO_PACIENTE, "pacienteId", PACIENTE_ID)
                .path("getHistoricoPaciente")
                .entity(PacienteDTO.class)
                .get();

        assertThat(paciente).isNotNull();
        assertThat(paciente.nome()).isNotBlank();
        assertThat(paciente.id()).isEqualTo(PACIENTE_ID);
    }
}