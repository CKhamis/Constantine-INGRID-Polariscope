package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import com.constantine.polariscope.Repository.EvaluationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    public List<Evaluation> findAllByMember(Member member){
        return evaluationRepository.findAllByMemberOrderByTimestampDesc(member);
    }

    public Optional<Evaluation> findById(UUID id){
        return evaluationRepository.findById(id);
    }

    public void save(Evaluation eval){
        evaluationRepository.save(eval);
    }
}
