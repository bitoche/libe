package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.services.NotificationService;

@Controller
@RestController
@RequestMapping("/notifications")
@Tag(name = "Просмотр уведомлений пользователя, создание уведомлений (как бы сообщений) для пользователя библиотекарем")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
@AllArgsConstructor
public class NotificationController {
    private final ResponseService rs;
    private final NotificationService notificationService;
    @Operation(summary = "Получить уведомления пользователя // perm:student, teacher")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getMYNotifications(@PathVariable Long userId){
        // todo spring security проверка если тот же то ок нет deny
        return rs.build(notificationService.getNotificationByUser(userId));
    }

    @Operation(summary = "Отправить уведомление пользователю // perm:librarian")
    @PostMapping("/l/send/{userId}")
    public ResponseEntity<?> createNotification(@PathVariable Long userId,
                                                @RequestParam String title,
                                                @RequestParam String text){
        return rs.build(notificationService.createNewNotification(title, text, userId));
    }

    @Operation(summary = "Отметить МОЕ уведомление прочитанным // perm:student, teacher")
    @GetMapping("/{userId}/check/{notificationId}")
    public ResponseEntity<?> checkNotification(@PathVariable Long userId, @PathVariable Long notificationId){
        // todo spring security проверка если тот же то ок нет deny
        var n = notificationService.getById(notificationId);
        if(n!=null){
            n.setChecked(true);
            return rs.build(notificationService.updateNotification(n));
        }
        return rs.build(null);
    }
}
