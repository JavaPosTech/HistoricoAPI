package br.com.fiap.historicoapi.model.dto.paciente;

import br.com.fiap.historicoapi.model.dto.agendamento.AgendamentoDTO;
import br.com.fiap.historicoapi.model.dto.historicopaciente.HistoricoPacienteDTO;
import br.com.fiap.historicoapi.model.entity.paciente.Paciente;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SchemaMapping("Paciente")
@Schema(description = "Representa o modelo de dados de um Paciente.")
public record PacienteDTO(

        Integer id,

        String nome,

        String sobrenome,

        String cpf,

        String email,

        String telefone,

        String endereco,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
        LocalDateTime dataCadastro,

        String situacaoCadastro,

        List<HistoricoPacienteDTO> historico,

        List<AgendamentoDTO> consultas

) {
    public PacienteDTO(Paciente paciente, List<HistoricoPacienteDTO> historico, List<AgendamentoDTO> consultas) {
        this(
                paciente.getId(),
                paciente.getNome(),
                paciente.getSobrenome(),
                paciente.getCpf(),
                paciente.getEmail(),
                paciente.getTelefone(),
                paciente.getEndereco(),
                paciente.getDataNascimento(),
                paciente.getDataCadastro(),
                paciente.getSituacaoCadastro().getDescricao(),
                historico,
                consultas
        );
    }
}