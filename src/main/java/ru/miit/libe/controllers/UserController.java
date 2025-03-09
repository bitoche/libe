package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
//import ru.miit.libe.dtos.RoleDTO;
//import ru.miit.libe.dtos.UserDTO;
import ru.miit.libe.dtos.AuthorizedLKDTO;
import ru.miit.libe.dtos.TeacherLKDTO;
import ru.miit.libe.dtos.UserLKDTO;
import ru.miit.libe.models.EUserRole;
import ru.miit.libe.models.User;
import ru.miit.libe.models.UserRole;
import ru.miit.libe.services.BorrowService;
import ru.miit.libe.services.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@RequestMapping("/api/user")
@Tag(name = "Контроллер для обычного пользователя // perm:all", description = "Позволяет смотреть пользователей и роли")
@CrossOrigin("http://localhost:3000/")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final BorrowService borrowService;
    private final ResponseService rs;

    @Operation(summary = "Достает всех пользователей из бд")
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAll(){
        return rs.build(userService.getAll());
    }

    @Operation(summary = "Достает пользователя по его ID")
    @GetMapping("/getUser/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId){
        return rs.build(userService.getById(userId));
    }

    @Operation(summary = "Достает все возможные роли пользователей")
    @GetMapping("/getAllUserRoles")
    public ResponseEntity<?> getAllRoles(){
        return rs.build(userService.getAllUserRoles());
    }

    @Operation(summary = "Возвращает DTO для ЛК пользователя")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getMYUserData(@PathVariable long userId){
        if (userService.existsById(userId)){
            //todo проверка по spring security - если не тот тогда forbidden
            var u = userService.getUser(userId, null);
            switch (u.getRole()){
                case AUTHORIZED -> {
                    var lk = new AuthorizedLKDTO(u);
                    return rs.build(lk);
                }
                case STUDENT -> {
                    var b = borrowService.findBorrowByUser(userId);
                    var lk = new UserLKDTO(u, b);
                    return rs.build(lk);
                }
                case TEACHER -> {
                    var b = borrowService.findBorrowByUser(userId);
                    var r = borrowService.getRequestBooksByUserId(userId);
                    var lk = new TeacherLKDTO(u, b, r);
                    return rs.build(lk);
                }
                default -> rs.build(null); // денай всех кто не входит в эти три категории
            }
        }
        return rs.build(null);
    }

}
