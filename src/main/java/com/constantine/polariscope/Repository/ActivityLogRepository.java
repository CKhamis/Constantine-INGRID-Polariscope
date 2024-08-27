package com.constantine.polariscope.Repository;

import com.constantine.polariscope.Model.ActivityLog;
import com.constantine.polariscope.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findAllByUserAndCreatedAfter(User user, LocalDateTime after);
    List<ActivityLog> findAllByUser(User user);
}
