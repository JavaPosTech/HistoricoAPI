package br.com.fiap.historicoapi.model.dto.paciente;

import br.com.fiap.historicoapi.model.dto.agendamento.AgendamentoDTO;
import br.com.fiap.historicoapi.model.dto.historicopaciente.HistoricoPacienteDTO;
import br.com.fiap.historicoapi.model.entity.paciente.Paciente;
import br.com.fiap.historicoapi.util.FormatadorData;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representa o modelo de dados de um Paciente.")
public record PacienteDTO(

        @Schema(description = "Identificador do paciente. No schema GraphQL é um ID! e trafega como String.", example = "1")
        Integer id,

        @Schema(description = "Primeiro nome do paciente", example = "Maria")
        String nome,

        @Schema(description = "Sobrenome do paciente", example = "Oliveira")
        String sobrenome,

        @Schema(description = "CPF do paciente", example = "123.456.789-00")
        String cpf,

        @Schema(description = "E-mail do paciente", example = "maria.oliveira@email.com")
        String email,

        @Schema(description = "Telefone de contato do paciente", example = "(11) 98765-4321")
        String telefone,

        @Schema(description = "Endereço do paciente", example = "Rua das Flores, 123 - São Paulo/SP")
        String endereco,

        @Schema(description = "Data de nascimento, no formato dd/MM/yyyy", example = "15/03/1990")
        String dataNascimento,

        @Schema(description = "Data do cadastro, no formato dd/MM/yyyy - HH:mm:ss", example = "10/01/2026 - 14:32:05")
        String dataCadastro,

        @Schema(description = "Descrição da situação do cadastro", example = "Ativo")
        String situacaoCadastro,

        @Schema(description = "Histórico clínico do paciente")
        List<HistoricoPacienteDTO> historico,

        @Schema(description = "Consultas agendadas para o paciente")
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
