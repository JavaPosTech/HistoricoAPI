package br.com.fiap.historicoapi.repository.paciente;

import br.com.fiap.historicoapi.model.entity.paciente.Paciente;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    @Override
    @EntityGraph(attributePaths = {"situacaoCadastro"})
    Optional<Paciente> findById(@NonNull Integer id);

}