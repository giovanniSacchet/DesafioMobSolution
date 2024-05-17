package br.com.mobsolution.events.repository;

import br.com.mobsolution.events.model.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long>, JpaSpecificationExecutor<Presence> {

    public List<Presence> findAllByParticipantId(Long id);

}
