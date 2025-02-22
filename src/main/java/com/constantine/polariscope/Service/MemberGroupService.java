package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.MemberGroup;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.EvaluationRepository;
import com.constantine.polariscope.Repository.MemberGroupRepository;
import com.constantine.polariscope.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MemberGroupService {
    private final MemberGroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final EvaluationRepository evaluationRepository;

    public List<MemberGroup> findAll(User author){
        List<MemberGroup> groups = groupRepository.findAllByAuthorOrderByLastModifiedDesc(author);
        for(MemberGroup group : groups){
            group.setNumMembers(memberRepository.countAllByGroup(group));
        }
        return groups;
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

    public List<Member> findMembersByGroup(MemberGroup group){
        List<Member> members = memberRepository.findAllByGroupOrderByLastModifiedDesc(group);
        for(Member member : members){
            List<Evaluation> allEvals = evaluationRepository.findAllByMemberOrderByTimestampDesc(member); // TODO: optimize this by maybe just storing the latest eval in the member itself
            if(!allEvals.isEmpty()){
                member.setMostRecentEval(allEvals.get(0));
            }
        }
        return members;
    }
}
