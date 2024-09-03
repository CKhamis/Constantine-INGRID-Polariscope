package com.constantine.polariscope.Service;

import com.constantine.polariscope.DTO.MemberListItem;
import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.EvaluationRepository;
import com.constantine.polariscope.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final EvaluationRepository evaluationRepository;

    public List<MemberListItem> allMembers(User author) throws Exception{
        List<Member> members = memberRepository.findAllByAuthorOrderByLastModifiedDesc(author);
        List<MemberListItem> memberListItems = new ArrayList<>();

        for (Member member : members) {
            MemberListItem listItem = new MemberListItem(member);
            List<Evaluation> allEvals = evaluationRepository.findAllByMemberOrderByTimestampDesc(member); // TODO: optimize this by maybe just storing the latest eval in the member itself
            if(!allEvals.isEmpty()){
                listItem.setLastEvaluation(allEvals.get(0));
            }

            memberListItems.add(listItem);
        }

        return memberListItems;
    }
    public Member save(Member member){
        return memberRepository.save(member);
    }

    public Member findMember(UUID id){
        return memberRepository.findById(id).orElseThrow();
    }

    public Member findMemberWithEval(UUID id){
        Member m = memberRepository.findById(id).orElseThrow();
        List<Evaluation> evals = evaluationRepository.findAllByMemberOrderByTimestampDesc(m);
        m.setTimeline(evals);
        m.setTimelineSize(evals.size());
        return m;
    }

    public List<Evaluation> findEvaluationByMember(Member member){
        return evaluationRepository.findAllByMemberOrderByTimestampDesc(member);
    }

    @Transactional
    public void deleteMember(Member member){
        evaluationRepository.deleteAllByMember(member);
        memberRepository.delete(member);
    }
}
