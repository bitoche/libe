package ru.miit.libe.controllers;

import jakarta.annotation.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.CreateUserRequest;
import ru.miit.libe.models.EUserRole;
import ru.miit.libe.models.User;
import ru.miit.libe.models.SAVETYPE;
import ru.miit.libe.services.UserService;

import java.sql.Date;


@Controller
@RestController
@RequestMapping("/api/super")
@Tag(name = "Управление ролями, созданием пользователей и т.д. // perm:admin")
@CrossOrigin("http://localhost:3000/")
public class AdminController {
    private final UserService userService;
    private final ResponseService rs;
    public AdminController(UserService userService, ResponseService rs) {
        this.userService = userService;
        this.rs = rs;
    }

    @Operation(summary = "Добавляет нового пользователя")
    @PostMapping("/createUser")
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
    @PutMapping("/changeUser")
    //@ApiResponse(responseCode = "444", description = "Пользователь не найден (UserService)")
    public ResponseEntity<?> updateUser(@RequestBody
                                           @Valid User user,
                                           @Parameter(description = "ID пользователя", required = true)
                                           @RequestParam Long userId){
        user.setUserId(userId); // обновим конкретного
        return rs.build(userService.update(user));
    }

    @Operation(summary = "Удаляет пользователя по ID (без подтверждения)")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
//            @ApiResponse(responseCode = "200", description = "Пользователь удален"),
//    })
    @DeleteMapping("/deleteUserById")
    public ResponseEntity<?> deleteUserById(@RequestParam Long userId){
        return rs.build(userService.deleteUserById(userId));
    }
}
