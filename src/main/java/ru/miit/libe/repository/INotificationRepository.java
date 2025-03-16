package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser_UserId(long userId);

    Optional<Notification> getNotificationByNotificationId(long notificationId);
}
