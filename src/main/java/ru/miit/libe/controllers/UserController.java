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
@Tag(name = "Управление пользователями и ролями", description = "Позволяет управлять пользователями и ролями")
@CrossOrigin("http://localhost:3000/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Operation(summary = "Достает всех пользователей из бд // perm: admin // now: all")
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAll(){
        //заменим полученных пользователей на userDTO, для того чтобы в роли не находились пользователи
        var allUsers = userService.getAll();
        //new UserDTO(user));
        List<User> resp = new ArrayList<>(allUsers);
        return ResponseEntity.ok(resp);
    }
    @Operation(summary = "Достает все возможные роли пользователя // perm: admin // now: all")
    @GetMapping("/getAllUserRoles")
    public ResponseEntity<?> getAllRoles(){
        var allRoles = userService.getAllUserRoles();
        List<UserRole> resp = new ArrayList<>();
        for (UserRole role : allRoles) {
            resp.add(role);//new RoleDTO(role));
        }
        return ResponseEntity.ok(resp);
    }
    @Operation(summary = "Добавляет нового пользователя // perm: admin // now: all")
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user){
        return userService.save(user, SAVETYPE.WITH_ROLE_INCLUDED)
                ? ResponseEntity.ok().body("Успешно создан пользователь с email = "+ user.getEmail())
                : ResponseEntity.badRequest().build();
    }
    @Operation(summary = "Достает пользователя по его ID // perm: all")
    @GetMapping("/getUser/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId){
        var resp = userService.getById(userId);
        if (resp != null){
            var resp1 = resp;//new UserDTO(resp);
            return ResponseEntity.ok(resp1);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

}
