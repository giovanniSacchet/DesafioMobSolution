package br.com.mobsolution.events.web;

import br.com.mobsolution.events.model.Event;
import br.com.mobsolution.events.model.Participant;
import br.com.mobsolution.events.model.Presence;
import br.com.mobsolution.events.service.EventService;
import br.com.mobsolution.events.service.ParticipantService;
import br.com.mobsolution.events.service.PresenceService;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@ViewScoped
@Getter
public class EventBean implements Serializable {

    @Autowired
    private EventService eventService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private PresenceService presenceService;

    private Pageable pageable;

    private Date today;
    private Event searchEvent;
    private Event selectedEvent;
    private Event backupEvent;

    private Participant newParticipant;
    private Participant selectedParticipant;
    private boolean showParticipantsTab;

    private Presence newPresence;
    private List<Presence> presencesParticipant;

    private List<Event> events;
    private List<Participant> participantsEvent;

    @PostConstruct
    public void init() {
        this.searchEvent = new Event();
        this.selectedEvent = new Event();
        this.backupEvent = new Event();

        this.newParticipant = new Participant();
        this.selectedParticipant = new Participant();

        this.newPresence = new Presence();

        this.showParticipantsTab = false;

        this.today = new Date();
        this.today.setHours(0);
        this.today.setMinutes(0);
        this.today.setSeconds(0);
        this.pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").ascending());
        this.events = this.eventService.findAll(this.pageable);
    }
    public boolean checkDatePresence() {
        this.presencesParticipant = this.presenceService.findAllByParticipantId(this.selectedParticipant.getId());
        for (Presence presence : this.presencesParticipant) {
            if (this.newPresence.getDate().compareTo(presence.getDate()) == 0) {
                return false;
            }
        }
        return true;
    }
    public void savePresenceParticipantEvent() {
        if (this.checkDatePresence()) {
            this.newPresence.setParticipantId(this.selectedParticipant.getId());
            this.presenceService.save(this.newPresence);
            this.newPresence = new Presence();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presença salva com sucesso!"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Participante já tem presença na data selecionada!"));
        }
    }
    public void search() {
        if ((this.searchEvent.getName() == null || this.searchEvent.getName().isEmpty())
                && this.searchEvent.getStartDate() == null
                && this.searchEvent.getEndDate() == null) {
            this.init();
        } else {
            this.events = this.eventService.find(this.searchEvent, this.pageable);
        }
    }
    public void closeModalEventDetailsDialog() {
        this.showParticipantsTab = false;
        this.selectedEvent = new Event();
        PrimeFaces.current().resetInputs("eventDetailsForm");
    }
    public void prepareNewEvent() {
        this.selectedEvent = new Event();
        PrimeFaces.current().resetInputs("eventDetailsForm");
        this.showParticipantsTab = false;
    }
    public void prepareSelectedEvent(Event event, String op) {
        this.showParticipantsTab = true;
        PrimeFaces.current().resetInputs("eventDetailsForm");
        this.selectedEvent = event;
        this.backupEvent = event;
        if (op.equals("details")) {
            this.participantsEvent = this.participantService.findAllByEventId(this.selectedEvent.getId(), this.pageable);
        }
    }
    public void prepareSelectedParticipant(Participant participant, String op) {
        if (op.equals("details")) {
            PrimeFaces.current().resetInputs("detailsParticipantForm");
        } else if (op.equals("presence")) {
            this.newPresence = new Presence();
            PrimeFaces.current().resetInputs("addPresenceForm");
        }
        this.selectedParticipant = participant;
    }
    public void loadModalParticipant() {
        this.newParticipant = new Participant();
        this.selectedParticipant = new Participant();
        PrimeFaces.current().resetInputs("eventDetailsForm");
        PrimeFaces.current().resetInputs("saveParticipantForm");
        PrimeFaces.current().resetInputs("detailsParticipantForm");
    }
    public long calculateDurationEvent(Event event) {
        Date startDate = event.getStartDate();
        Date endDate = event.getEndDate();

        // Zerar o tempo das datas para considerar apenas os dias
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        long differenceMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
        return TimeUnit.DAYS.convert(differenceMillis, TimeUnit.MILLISECONDS) + 1;
    }
    public String formatEventDuration(Event event) {
        return this.calculateDurationEvent(event) + " Dias";
    }
    public String getFormattedStartDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = this.backupEvent.getStartDate();
        return sdf.format(startDate);
    }
    public void saveEvent() {
        // Falta validar campos data ao editar evento...
        if (this.selectedEvent.getId() == null && this.selectedEvent.getStartDate().before(this.today)) {
            System.out.println("fui");
        }
        if (this.selectedEvent.isValid() ) {
            this.showParticipantsTab = true;
            if (this.selectedEvent.getId() == null) {
                this.participantsEvent = new ArrayList<>();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento cadastrado com sucesso!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento alterado com sucesso!"));
            }
            this.selectedEvent = this.eventService.save(this.selectedEvent);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Evento em formato invalido! Verificar os campos Data..."));
        }
        this.events = this.eventService.findAll(this.pageable);
    }
    public void deleteAllPresencesParticipant(Long id) {
        this.presencesParticipant = new ArrayList<>();
        this.presencesParticipant = this.presenceService.findAllByParticipantId(id);
        if (!this.presencesParticipant.isEmpty()) {
            for (Presence presence : this.presencesParticipant) {
                this.presenceService.delete(presence);
            }
        }
    }
    public void deleteAllParticipantsByEvent() {
        for (Participant participant : this.participantsEvent) {
            this.deleteAllPresencesParticipant(participant.getId());
            this.participantService.delete(participant);
        }
    }
    public void deleteEvent() {
        this.participantsEvent = this.participantService.findAllByEventId(this.selectedEvent.getId(), pageable);
        if (!this.participantsEvent.isEmpty()) {
            this.deleteAllParticipantsByEvent();
        }
        this.eventService.delete(this.selectedEvent);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento excluido com sucesso!"));
        this.init();
    }
    public String calculateParticipationEvent(Participant participant) {
        long numDays = this.calculateDurationEvent(this.selectedEvent);
        List<Presence> numPresences = this.presenceService.findAllByParticipantId(participant.getId());
        long presenceDays = numPresences.size();
        return String.format("%.2f", (double) presenceDays / numDays * 100);
    }
    public boolean checkExistingCpf(Participant currentParticipant, String op) {
        Optional<Participant> participant = this.participantService.findByCpfAndEventId(currentParticipant.getCpf(), this.selectedEvent.getId());
        if (participant.isPresent()) {
            if (op.equals("edit") && this.selectedParticipant.getId().equals(participant.get().getId())) {
                return false;
            }
        }
        return participant.isPresent();
    }
    public void saveParticipant(Participant participant, String op) {
        if (this.checkExistingCpf(participant, op)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Participante com o CPF: " + participant.getCpf() + " já cadastrado."));
            this.loadModalParticipant();
            this.participantsEvent = this.participantService.findAllByEventId(this.selectedEvent.getId(), this.pageable);
            return;
        }
        participant.setEventId(this.selectedEvent.getId());
        Participant participantBd = this.participantService.save(participant);
        if (participantBd.isValid()) {
            if (op.equals("new")) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Participante cadastrado com sucesso!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Participante alterado com sucesso!"));
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Por favor, preencha todos os campos obrigatórios."));
            return;
        }
        this.participantsEvent = this.participantService.findAllByEventId(this.selectedEvent.getId(), pageable);
        this.loadModalParticipant();
    }
    public void saveNewParticipant() {
        if (this.newParticipant.isValid()) {
            this.saveParticipant(this.newParticipant, "new");
        }
    }
    public void saveEditedParticipant() {
        if (this.selectedParticipant.isValid()) {
            this.saveParticipant(this.selectedParticipant, "edit");
        }
    }
    public void deleteParticipant() {
        this.deleteAllPresencesParticipant(this.selectedParticipant.getId());
        this.participantService.delete(this.selectedParticipant);
        this.participantsEvent = this.participantService.findAllByEventId(this.selectedEvent.getId(), this.pageable);
        this.selectedParticipant = new Participant();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Participante excluido com sucesso!"));
    }
}