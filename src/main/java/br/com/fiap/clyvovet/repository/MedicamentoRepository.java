package br.com.fiap.clyvovet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.clyvovet.model.Medicamento;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

	List<Medicamento> findByPetIdOrderByDataInicioDesc(Long idPet);

	List<Medicamento> findByConsultaIdOrderByNome(Long idConsulta);

}
