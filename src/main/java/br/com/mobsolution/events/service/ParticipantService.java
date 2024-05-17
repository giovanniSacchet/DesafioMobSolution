package br.com.mobsolution.events.service;

import br.com.mobsolution.events.model.Participant;
import br.com.mobsolution.events.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public List<Participant> findAllByEventId(Long id, Pageable pageable) {
        return this.participantRepository.findAllByEventId(id, pageable);
    }

    public Optional<Participant> findByCpfAndEventId(String cpf, Long id) {
        return this.participantRepository.findByCpfAndEventId(cpf, id);
    }
    public Participant save(Participant participant) {
        return this.participantRepository.save(participant);
    }

    public void delete(Participant participant) {
        this.participantRepository.delete(participant);
    }
}
