package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Group;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.GroupRepository;
import com.constantine.polariscope.Repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public List<Group> findAll(User author){
        return groupRepository.findAllByAuthorOrderByLastModifiedDesc(author);
    }
    public Group saveGroup(Group group){
        return groupRepository.save(group);
    }
    public void deleteGroup(Group group){
        // First, remove all associations to the group
        memberRepository.deleteAllByGroup(group);
        // Now delete the group itself
        groupRepository.delete(group);
    }
    public Optional<Group> findGroupById(UUID id){
        return groupRepository.findById(id);
    }
}
