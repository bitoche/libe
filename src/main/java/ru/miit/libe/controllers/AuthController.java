package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.CreateUserRequest;
import ru.miit.libe.services.SAVETYPE;
import ru.miit.libe.services.UserService;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/auth")
@Tag(name = "Управление входом, аутентификацией, регистрацией")
@CrossOrigin("http://localhost:3000/")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger("Main");

    private final UserService userService;

    private org.springframework.security.core.userdetails.UserDetails getUserDetails(){
        // Получаем объект аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Получаем principal (обычно это объект UserDetails)
        Object principal = authentication.getPrincipal();

        // Проверяем, что principal является UserDetails
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return (org.springframework.security.core.userdetails.UserDetails) principal;
        } else {
            return null;
        }
    }

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<?> registerUser(
            @RequestParam String firstName,
            @RequestParam String secondName,
            @RequestParam @Nullable String thirdName,
            @RequestParam @Nullable Date birthDate,
            @RequestParam String email,
            @RequestParam String password){
        CreateUserRequest user = new CreateUserRequest();
        if(!userService.existsByRoleName("DEACTIVATED")){
            userService.saveUserRole("DEACTIVATED");
        } // если роли DEACTIVATED не существует - создаем
        user.setRoleName("DEACTIVATED"); // делаем учетку неактивированной, дальше todo привязать логику деактивированной учетки
        // здесь должен вызываться метод создания кода для входа,
        // с помощью которого будет осуществляться подтверждение учетной записи
        // этот код должен быть длиннее, чем при обычном входе в аккаунт.
        user.setPassword(password);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setSecondName(secondName);
        if (thirdName!=null && !thirdName.isEmpty()){
            user.setThirdName(thirdName);
        }
        if (birthDate!=null){
            user.setBirthDate(birthDate);
        }
        return userService.save(user, SAVETYPE.STANDARD_REGISTER) ? ResponseEntity.ok(user) : ResponseEntity.badRequest().body("Error in registering. Perhaps, username exists.");
    }


    @GetMapping("/check-email-availability")
    @ResponseBody
    public Map<String, Boolean> checkUsernameAvailability(@RequestParam("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !userService.existsByUsername(email));
        return response;
    }

    @GetMapping("/check-adm-privileges")
    public ResponseEntity<?> checkAdmPriv(){
        return ResponseEntity.ok("У вас есть привелении администратора");
    }
    @GetMapping("/check-teacher-privileges")
    public ResponseEntity<?> checkTeacherPriv(){
        return ResponseEntity.ok("У вас есть привелении преподавателя");
    }
    @GetMapping("/check-dev-privileges")
    public ResponseEntity<?> checkDevPriv(){
        return ResponseEntity.ok("У вас есть привелении разработчика");
    }

    @Operation(
            summary = "Выход из системы",
            description = "Завершает текущую сессию пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный выход"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Этот метод теперь не нужен, так как выход обрабатывается Spring Security
        return ResponseEntity.ok("Выход обрабатывается Spring Security");
    }

    @GetMapping("/login") // переадресация на страницу входа
    public ResponseEntity<?> login(){
        return ResponseEntity.ok("*todo переход на страницу входа*"); // todo сделать переход на страницу входа
    }
    @Operation(summary = "Ввод entryCode !ПОСЛЕ РЕГИСТРАЦИИ!, требует логин и пароль пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Учетная запись подтверждена"),
            @ApiResponse(responseCode = "202", description = "Пользователь уже зарегистрирован"),
            @ApiResponse(responseCode = "300", description = "Неверный entryCode"),
            @ApiResponse(responseCode = "301", description = "Пользователя не существует"),
            @ApiResponse(responseCode = "303", description = "Кода не существовало. Выдан новый entryCode"),
            @ApiResponse(responseCode = "302", description = "Неверный пароль"),
            @ApiResponse(responseCode = "310", description = "EntryCode устарел. Выслан новый")
    })
    @PostMapping("/codeAfterRegister")
    public ResponseEntity<?> getAccessToAccount(@RequestParam @NotNull String entryCode,
                                                @RequestParam @NotNull String email,
                                                @RequestParam @NotNull String password){
        return userService.checkAccess(entryCode, email, password, true);
    }

    @Operation(summary = "Получить код для входа в аккаунт")
    @PostMapping("/getOneTimeCode")
    public ResponseEntity<?> getOneTimeCode(@NotNull String email){
        return userService.getOneTimeCode(email);
    }

    @Operation(summary = "Вход в аккаунт")
    @PostMapping("/loginProcessing")
    public ResponseEntity<?> login(@NotNull String email, @NotNull String password, @NotNull String entryCode){
        var resp = userService.loginWithOneTimeCode(email, password, entryCode);
        return resp ? ResponseEntity.ok().body("Все ок") : ResponseEntity.badRequest().body("Что-то неверное.");
    }

    @Operation(summary = "test me")
    @GetMapping("/testme")
    public ResponseEntity<?> testme(){
        // Получаем аутентификацию из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверяем, аутентифицирован ли пользователь
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok().body(authentication.getPrincipal());
        }

        // Если пользователь не аутентифицирован, возвращаем 204 No Content
        return ResponseEntity.noContent().build();
    }
}

