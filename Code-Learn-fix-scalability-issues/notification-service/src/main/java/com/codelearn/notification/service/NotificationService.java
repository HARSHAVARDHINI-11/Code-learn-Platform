package com.codelearn.notification.service;

import com.codelearn.notification.model.Notification;
import com.codelearn.notification.repository.NotificationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Cacheable(value = "notifications", key = "#id")
    @CircuitBreaker(name = "notificationService")
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    @CircuitBreaker(name = "notificationService")
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @CircuitBreaker(name = "notificationService")
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndRead(userId, false);
    }

    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    @CircuitBreaker(name = "notificationService")
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional
    @CacheEvict(value = "notifications", key = "#id")
    @CircuitBreaker(name = "notificationService")
    public Optional<Notification> markAsRead(Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setRead(true);
                    return notificationRepository.save(notification);
                });
    }

    @RabbitListener(queues = "notification.queue")
    public void handleNotificationEvent(String message) {
        // Process notification events from RabbitMQ
        System.out.println("Received notification event: " + message);
    }
}
