package com.constantine.polariscope.Service;

import com.constantine.polariscope.Model.ActivityLog;
import com.constantine.polariscope.Model.User;
import com.constantine.polariscope.Repository.ActivityLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class ActivityLogService {
    private ActivityLogRepository activityLogRepository;

    public List<ActivityLog> findAll(User user){
        return activityLogRepository.findAllByUserOrderByCreatedAsc(user);
    }

    public List<ActivityLog> findAllAfter(User user, LocalDateTime after){
        return activityLogRepository.findAllByUserAndCreatedAfterOrderByCreatedAsc(user, after);
    }

    public ActivityLog save(ActivityLog log){
        return activityLogRepository.save(log);
    }

}
