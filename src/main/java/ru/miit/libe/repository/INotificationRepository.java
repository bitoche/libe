package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.Notification;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {
}
