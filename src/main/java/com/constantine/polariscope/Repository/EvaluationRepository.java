package com.constantine.polariscope.Repository;

import com.constantine.polariscope.Model.Evaluation;
import com.constantine.polariscope.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
    List<Evaluation> findAllByMemberOrderByTimestampDesc(Member member);
    int countAllByMember(Member member);
}
