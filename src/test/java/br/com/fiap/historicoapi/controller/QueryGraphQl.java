package br.com.fiap.historicoapi.controller;

final class QueryGraphQl {

    static final String HISTORICO_PACIENTE = """
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

    private QueryGraphQl() {}
}
