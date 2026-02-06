package org.votingsystem.voting_system_for_award_nominations.service.em;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.votingsystem.voting_system_for_award_nominations.modelentity.Event;
import org.votingsystem.voting_system_for_award_nominations.modelentity.VotingSession;
import org.votingsystem.voting_system_for_award_nominations.repository.EventRepository;
import org.votingsystem.voting_system_for_award_nominations.repository.VotingSessionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final VotingSessionRepository votingSessionRepository;

    public EventService(EventRepository eventRepository, VotingSessionRepository votingSessionRepository) {
        this.eventRepository = eventRepository;
        this.votingSessionRepository = votingSessionRepository;
    }

    //  Create new event (and close any existing active event if needed)
    public Event createEvent(Event event) {
        // Default to ACTIVE if no status set
        if (event.getStatus() == null || event.getStatus().isBlank()) {
            event.setStatus("ACTIVE");
        }

        // Ensure only one active event at a time
        List<Event> activeEvents = eventRepository.findByStatus("ACTIVE");
        for (Event active : activeEvents) {
            active.setStatus("CLOSED");
            eventRepository.save(active);

            //  End any ongoing session for the old active event
            votingSessionRepository.findTopByEvent_IdOrderByIdDesc(active.getId())
                    .ifPresent(session -> {
                        if ("ONGOING".equalsIgnoreCase(session.getStatus())) {
                            session.setStatus("ENDED");
                            votingSessionRepository.save(session);
                        }
                    });
        }

        return eventRepository.save(event);
    }

    //  Get the currently active event
    public Optional<Event> getActiveEvent() {
        return eventRepository.findFirstByStatus("ACTIVE");
    }

    //  Get single event by ID (useful for admin actions)
    public Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    //  List all events
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    //  Close an event manually (and end its session if ongoing)
    public void closeEvent(Long eventId) {
        eventRepository.findById(eventId).ifPresent(event -> {
            event.setStatus("CLOSED");
            eventRepository.save(event);

            //  End the session too if still ONGOING
            votingSessionRepository.findTopByEvent_IdOrderByIdDesc(eventId)
                    .ifPresent(session -> {
                        if ("ONGOING".equalsIgnoreCase(session.getStatus())) {
                            session.setStatus("ENDED");
                            votingSessionRepository.save(session);
                        }
                    });
        });
    }

    //  Update event details (only if ACTIVE)
    public void updateEvent(Long eventId, Event updatedEvent) {
        eventRepository.findById(eventId).ifPresent(event -> {
            if ("ACTIVE".equals(event.getStatus())) {
                event.setName(updatedEvent.getName());
                event.setEventDate(updatedEvent.getEventDate());
                event.setDescription(updatedEvent.getDescription());
                eventRepository.save(event);
            }
        });
    }
}
