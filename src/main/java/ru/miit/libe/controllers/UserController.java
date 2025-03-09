package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
//import ru.miit.libe.dtos.RoleDTO;
//import ru.miit.libe.dtos.UserDTO;
import ru.miit.libe.models.EUserRole;
import ru.miit.libe.models.User;
import ru.miit.libe.models.UserRole;
import ru.miit.libe.services.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@RequestMapping("/api/user")
@Tag(name = "Контроллер для обычного пользователя // perm:all", description = "Позволяет смотреть пользователей и роли")
@CrossOrigin("http://localhost:3000/")
public class UserController {
    private final UserService userService;
    private final ResponseService rs;
    public UserController(UserService userService, ResponseService rs) {
        this.userService = userService;
        this.rs=rs;
    }

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

}
