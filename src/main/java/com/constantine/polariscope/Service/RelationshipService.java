package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Relationship;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.RelationshipRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RelationshipService {
    private final RelationshipRepository relationshipRepository;

    public void save(Relationship relationship) {
        relationshipRepository.save(relationship);
    }
    public List<Relationship> findAll(User author) {
        return relationshipRepository.findAllByAuthor_Id(author.getId());
    }
    public Optional<Relationship> findById(UUID id) {return relationshipRepository.findById(id);}
    public List<Relationship> findAllByMember(UUID memberId){return relationshipRepository.findAllBySelf_Id(memberId);}
    public void delete(UUID id) {
        relationshipRepository.deleteById(id);
    }
}
