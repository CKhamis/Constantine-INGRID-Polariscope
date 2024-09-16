package com.constantine.polariscope.Service;
import com.constantine.polariscope.DTO.UserSaveData;
import com.constantine.polariscope.Model.*;
import com.constantine.polariscope.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class SaveDataService {
    ActivityLogRepository activityLogRepository;
    EvaluationRepository evaluationRepository;
    MemberGroupRepository memberGroupRepository;
    MemberRepository memberRepository;

    public byte[] retrieveUserData(User user) throws IOException {
        ArrayList<ActivityLog> allLogs = new ArrayList<>(activityLogRepository.findAllByUserOrderByCreatedAsc(user));
        ArrayList<Member> allMembers = new ArrayList<>(memberRepository.findAllByAuthorOrderByLastModifiedDesc(user));
        ArrayList<MemberGroup> allMemberGroups = new ArrayList<>(memberGroupRepository.findAllByAuthorOrderByLastModifiedDesc(user));

        ArrayList<Evaluation> allEvaluations = new ArrayList<>();
        for(Member member : allMembers) {
            allEvaluations.addAll(evaluationRepository.findAllByMemberOrderByTimestampDesc(member));
        }

        UserSaveData userSaveData = new UserSaveData(user, allMembers, allLogs, allEvaluations, allMemberGroups);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        return baos.toByteArray();
    }


}
