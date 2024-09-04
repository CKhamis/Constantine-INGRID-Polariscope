package com.constantine.polariscope.Repository;

import com.constantine.polariscope.Model.Group;
import com.constantine.polariscope.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    List<Group> findAllByAuthorOrderByLastModifiedDesc(User user);
}
