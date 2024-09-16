package com.constantine.polariscope.Service;
import com.constantine.polariscope.DTO.UserSaveData;
import com.constantine.polariscope.Model.*;
import com.constantine.polariscope.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
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
        List<ActivityLog> rat = activityLogRepository.findAllByUserOrderByCreatedAsc(user);
        ArrayList<ActivityLog> allLogs = new ArrayList<>(rat);
        ArrayList<Member> allMembers = new ArrayList<>(memberRepository.findAllByAuthorOrderByLastModifiedDesc(user));
        ArrayList<MemberGroup> allMemberGroups = new ArrayList<>(memberGroupRepository.findAllByAuthorOrderByLastModifiedDesc(user));

        ArrayList<Evaluation> allEvaluations = new ArrayList<>();
        for(Member member : allMembers) {
            allEvaluations.addAll(evaluationRepository.findAllByMemberOrderByTimestampDesc(member));
        }

        UserSaveData userSaveData = new UserSaveData(user, allMembers, allLogs, allEvaluations, allMemberGroups);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(userSaveData);
        return baos.toByteArray();
    }

    public void importUserData(byte[] saveDataBytes) throws ClassNotFoundException, IOException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(saveDataBytes));
        UserSaveData data = (UserSaveData) ois.readObject();

        if(data.VERSION.equals(UserSaveData.VERSION)) {
            activityLogRepository.saveAll(data.getActivity());
            memberRepository.saveAll(data.getMembers());
            evaluationRepository.saveAll(data.getEvaluations());
            memberGroupRepository.saveAll(data.getGroups());
        }
    }
}
