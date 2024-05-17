package br.com.mobsolution.events.repository;

import br.com.mobsolution.events.model.Participant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long>, JpaSpecificationExecutor<Participant> {

    public List<Participant> findAllByEventId(Long id, Pageable pageable);

    public Optional<Participant> findByCpfAndEventId(String cpf, Long id);
}
