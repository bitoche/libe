package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.CreateUserRequest;
//import ru.miit.libe.dtos.RoleDTO;
//import ru.miit.libe.dtos.UserDTO;
import ru.miit.libe.models.User;
import ru.miit.libe.models.UserRole;
import ru.miit.libe.services.SAVETYPE;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Достает всех пользователей из бд")
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAll(){
        var allUsers = userService.getAll();
        List<User> resp = new ArrayList<>(allUsers);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Достает пользователя по его ID")
    @GetMapping("/getUser/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId){
        var resp = userService.getById(userId);
        if (resp != null){
            var resp1 = resp;
            return ResponseEntity.ok(resp1);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Достает все возможные роли пользователей")
    @GetMapping("/getAllUserRoles")
    public ResponseEntity<?> getAllRoles(){
        var allRoles = userService.getAllUserRoles();
        List<UserRole> resp = new ArrayList<>(allRoles);
        return ResponseEntity.ok(resp);
    }

}
