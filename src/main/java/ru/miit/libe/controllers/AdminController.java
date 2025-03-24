package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.CreateUserRequest;
import ru.miit.libe.dtos.UpdateUserRequest;
import ru.miit.libe.models.EUserRole;
import ru.miit.libe.dtos.SAVETYPE;
import ru.miit.libe.services.UserService;

import java.sql.Date;


@Controller
@RestController
@RequestMapping("/admin")
@Tag(name = "Управление ролями, созданием пользователей и т.д. // perm:admin")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
@SecurityRequirement(name = "BearerAuth")
public class AdminController {
    private final UserService userService;
    private final ResponseService rs;
    public AdminController(UserService userService, ResponseService rs) {
        this.userService = userService;
        this.rs = rs;
    }

    @Operation(summary = "Добавляет нового пользователя")
    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@RequestParam String firstName,
                                        @RequestParam String secondName,
                                        @RequestParam @Nullable String thirdName,
                                        @RequestParam @Nullable Date birthDate,
                                        @RequestParam String email,
                                        @RequestParam String password,
                                        @RequestParam EUserRole role){
        CreateUserRequest crr = new CreateUserRequest(firstName,
                secondName,
                thirdName,
                birthDate,
                email,
                password,
                role);
        return rs.build(userService.save(crr, SAVETYPE.WITH_ROLE_INCLUDED));
    }

    @Operation(summary = "Изменить данные пользователя (пароль хешируется после выполнения)")
    @PutMapping("/users/change")
    public ResponseEntity<?> updateUser(@RequestBody
                                        UpdateUserRequest updatedUser){
        return rs.build(userService.update(updatedUser));
    }

    @Operation(summary = "Удаляет пользователя по ID (без подтверждения)")
    @DeleteMapping("/users/delete")
    public ResponseEntity<?> deleteUserById(@RequestParam Long userId){
        return rs.build(userService.deleteUserById(userId));
    }
}
