package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public List<Member> allMembers(User author) throws Exception{
        return memberRepository.findAllByAuthorOrderByLastModifiedDesc(author);
    }
    public void save(Member member){
        memberRepository.save(member);
    }
}
