package br.com.fiap.historicoapi.exceptions;

public class PacienteNaoEncontradoException extends RuntimeException {

    public PacienteNaoEncontradoException(Integer pacienteId) {
        super("Paciente não encontrado - ID: " + pacienteId);
    }
}