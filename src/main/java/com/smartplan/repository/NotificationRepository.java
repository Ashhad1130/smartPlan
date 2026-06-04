package com.smartplan.repository;

import com.smartplan.model.Notification;
import com.smartplan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndLuFalse(User user);
    List<Notification> findByUser(User user);
}
