package com.constantine.polariscope.Repository;

import com.constantine.polariscope.Model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, String> {
    List<Relationship> findAllByAuthor_Id(UUID authorId);
    List<Relationship> findAllBySelf_Id(UUID selfId);
}
