package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.MemberGroup;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.MemberGroupRepository;
import com.constantine.polariscope.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MemberGroupService {
    private final MemberGroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public List<MemberGroup> findAll(User author){
        return groupRepository.findAllByAuthorOrderByLastModifiedDesc(author);
    }
    public MemberGroup saveGroup(MemberGroup group){
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(MemberGroup group){
        // First, remove all associations to the group
        List<Member> members = memberRepository.findAllByGroupOrderByLastModifiedDesc(group);
        for(Member member : members){
            member.setGroup(null);
            memberRepository.save(member);
        }
        // Now delete the group itself
        groupRepository.delete(group);
    }
    public Optional<MemberGroup> findGroupById(UUID id){
        return groupRepository.findById(id);
    }
}
