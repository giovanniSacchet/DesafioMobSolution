package br.com.mobsolution.events.service;

import org.apache.commons.lang3.StringUtils;
import br.com.mobsolution.events.model.Event;
import br.com.mobsolution.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> findAll(Pageable pageable) {
        return this.eventRepository.findAll(pageable).getContent();
    }
    public Event save(Event event) { return this.eventRepository.save(event); }
    public void delete(Event event) { this.eventRepository.delete(event); }
    public List<Event> find(Event dto, Pageable pageable) {

        Specification<Event> spec = null;

        String name = dto.getName();
        if (StringUtils.isNotBlank(name)) {
            spec = EventRepository.byName(name.trim());
        }

        Date startDate = dto.getStartDate();
        if (startDate != null) {
            Specification<Event> byStartDate = EventRepository.byStartDate(startDate);
            spec = spec != null ? spec.and(byStartDate) : byStartDate;
        }

        Date endDate = dto.getEndDate();
        if (endDate != null) {
            Specification<Event> byEndDate = EventRepository.byEndDate(endDate);
            spec = spec != null ? spec.and(byEndDate) : byEndDate;
        }

        return spec != null ?
                eventRepository.findAll(spec, pageable).getContent() :
                eventRepository.findAll(pageable).getContent();
    }
}
