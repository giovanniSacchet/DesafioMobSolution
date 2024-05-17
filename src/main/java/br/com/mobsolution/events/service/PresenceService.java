package br.com.mobsolution.events.service;

import br.com.mobsolution.events.model.Presence;
import br.com.mobsolution.events.repository.PresenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PresenceService {

    @Autowired
    private PresenceRepository presenceRepository;

    public List<Presence> findAllByParticipantId(Long id) {
        return this.presenceRepository.findAllByParticipantId(id);
    }

    public Presence save(Presence presence) {
        return this.presenceRepository.save(presence);
    }

    public void delete(Presence presence) {
        this.presenceRepository.delete(presence);
    }
}
