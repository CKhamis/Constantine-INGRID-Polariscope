package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Event;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.EvaluationRepository;
import com.constantine.polariscope.Repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> findAllByAuthor(User user) {
        return eventRepository.findAllByAuthor(user);
    }

    public void delete(Event event) {
        eventRepository.delete(event);
    }
}
