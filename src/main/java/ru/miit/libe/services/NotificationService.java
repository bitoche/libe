package ru.miit.libe.services;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import ru.miit.libe.dtos.MailMessageDTO;
import ru.miit.libe.models.EntryCode;
import ru.miit.libe.models.Notification;
import ru.miit.libe.repository.INotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    @Autowired
    INotificationRepository notificationRepository;
    UserService userService;
    EmailService emailService;

    private void sendNotificationToEmail(Notification n) throws MessagingException {
        var message = new MailMessageDTO(n);
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom(message.getSender());
//        mailMessage.setTo(message.getReceiver());
//        mailMessage.setSubject(message.getMailTitle());
//        mailMessage.setText(message.getTextMessage());
        emailService.sendEmail(message.getSender(), message.getReceiver(), message.getMailTitle(), message.getTextMessage());
    }

    public Notification createNewNotification(@NotNull String title,@NotNull String text, @NotNull Long userId){
        var not = new Notification();
        not.setNotificationDttm(LocalDateTime.now());
        var u = userService.getById(userId);
        if(u==null){
            return null;
        }
        not.setUser(u);
        not.setTitle(title);
        not.setText(text);
        not.setChecked(false);
        notificationRepository.save(not);
        // отправка на почту
        try{
            sendNotificationToEmail(not);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return not;
    }

    public List<Notification> getNotificationByUser(@NotNull Long userId){
        var u = userService.getById(userId);
        if(u!=null){
            return notificationRepository.findAllByUser_UserId(userId);
        }
        return null;
    }

    public Notification updateNotification(@NotNull Notification notification){
        var u = userService.getById(notification.getUser().getUserId());
        if(u!=null){
            var notif = notificationRepository.getNotificationByNotificationId(notification.getNotificationId());
            if(notif.isPresent()){
                var n = notif.get();
                notification.setNotificationId(n.getNotificationId());
                notificationRepository.save(notification);
                return notification;
            }
        }
        return null;
    }

    public Notification getById(Long notificationId){
        return notificationRepository.getNotificationByNotificationId(notificationId)
                .orElse(null);
    }
}
