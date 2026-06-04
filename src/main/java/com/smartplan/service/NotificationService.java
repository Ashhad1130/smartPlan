package com.smartplan.service;

import com.smartplan.model.Notification;
import com.smartplan.model.User;
import com.smartplan.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndLuFalse(user);
    }
    
    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findByUser(user);
    }
    
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }
    
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
