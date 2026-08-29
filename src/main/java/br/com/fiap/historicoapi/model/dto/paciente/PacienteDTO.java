package br.com.fiap.historicoapi.model.dto.paciente;

import br.com.fiap.historicoapi.model.dto.agendamento.AgendamentoDTO;
import br.com.fiap.historicoapi.model.dto.historicopaciente.HistoricoPacienteDTO;
import br.com.fiap.historicoapi.model.entity.paciente.Paciente;
import br.com.fiap.historicoapi.util.FormatadorData;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representa o modelo de dados de um Paciente.")
public record PacienteDTO(

        Integer id,

        String nome,

        String sobrenome,

        String cpf,

        String email,

        String telefone,

        String endereco,

        String dataNascimento,

        String dataCadastro,

        String situacaoCadastro,

        List<HistoricoPacienteDTO> historico,

        List<AgendamentoDTO> consultas

) {
    public PacienteDTO {
        historico = historico == null ? List.of() : List.copyOf(historico);
        consultas = consultas == null ? List.of() : List.copyOf(consultas);
    }

    public static PacienteDTO from(Paciente paciente,
                                   List<HistoricoPacienteDTO> historico,
                                   List<AgendamentoDTO> consultas) {
        return new PacienteDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getSobrenome(),
                paciente.getCpf(),
                paciente.getEmail(),
                paciente.getTelefone(),
                paciente.getEndereco(),
                FormatadorData.formatar(paciente.getDataNascimento()),
                FormatadorData.formatar(paciente.getDataCadastro()),
                paciente.getSituacaoCadastro().getDescricao(),
                historico,
                consultas
        );
    }
}
