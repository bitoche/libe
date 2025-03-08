package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.CreateUserRequest;
import ru.miit.libe.models.User;
import ru.miit.libe.models.UserRole;
import ru.miit.libe.services.SAVETYPE;
import ru.miit.libe.services.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@RequestMapping("/api/super")
@Tag(name = "Управление ролями, созданием пользователей и т.д. // perm:admin")
@CrossOrigin("http://localhost:3000/")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Добавляет нового пользователя")
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user){
        return ResponseEntity.ok().body(userService.save(user, SAVETYPE.WITH_ROLE_INCLUDED));
    }

    @Operation(summary = "Изменить данные пользователя (пароль хешируется после выполнения)")
    @PutMapping("/changeUser")
    @ApiResponse(responseCode = "444", description = "Пользователь не найден (UserService)")
    public ResponseEntity<User> updateUser(@RequestBody
                                           @Valid User user,
                                           @Parameter(description = "ID пользователя", required = true)
                                           @RequestParam Long userId){
        user.setUserId(userId); // обновим конкретного
        return userService.update(user) != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.status(444).build();
    }

    @Operation(summary = "Добавляет новую роль пользователя")
    @PostMapping("/createUserRole")
    public ResponseEntity<?> createUserRole(@RequestParam String roleName){
        var resp = userService.saveUserRole(roleName);
        if (resp != null){
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.badRequest().body("role " + roleName +" already exists");
    }

    @Operation(summary = "Удаляет пользователя по ID (без подтверждения)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "200", description = "Пользователь удален"),
    })
    @DeleteMapping("/deleteUserById")
    public ResponseEntity<?> deleteUserById(@RequestParam Long userId){
        if (userService.getById(userId)!=null){
            return ResponseEntity.ok().body(userService.deleteUserById(userId));
        }
        else return ResponseEntity.notFound().build();
    }
}
