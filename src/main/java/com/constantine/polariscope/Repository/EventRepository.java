package com.constantine.polariscope.Repository;

import com.constantine.polariscope.Model.Event;
import com.constantine.polariscope.Model.MemberGroup;
import com.constantine.polariscope.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByAuthor(User author);
}
