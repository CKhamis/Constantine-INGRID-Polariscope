package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Repository.EvaluationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    public List<Evaluation> findAllByMember(Member member){
        return evaluationRepository.findAllByMemberOrderByTimestampDesc(member);
    }
}
