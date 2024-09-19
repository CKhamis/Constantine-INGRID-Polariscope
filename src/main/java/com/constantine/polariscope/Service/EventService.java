package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Event;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.EventRepository;
import com.constantine.polariscope.Repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> findAllByAuthor(User user) {
        return eventRepository.findAllByAuthor(user);
    }

    public Optional<Event> findById(UUID id) {
        return eventRepository.findById(id);
    }

    public void delete(Event event) {
        // Remove associations with the event
//        event.getMembers().clear();
//        eventRepository.save(event);

        Set<Member> memberList = event.getMembers();

        memberList.forEach((m) -> {
            m.getEvents().remove(event);
            memberRepository.save(m);
        });

        eventRepository.delete(event);
    }
}
