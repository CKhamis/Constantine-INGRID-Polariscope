package com.constantine.polariscope.Repository;

import com.constantine.polariscope.Model.Place;
import com.constantine.polariscope.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<Place, UUID> {
    List<Place> findAllByAuthorOrderByLastModifiedDesc(User user);
}
