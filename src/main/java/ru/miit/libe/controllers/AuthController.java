package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.CreateUserRequest;
import ru.miit.libe.models.AppUserDetails;
import ru.miit.libe.models.EUserRole;
import ru.miit.libe.dtos.SAVETYPE;
import ru.miit.libe.models.User;
import ru.miit.libe.services.UserService;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/auth")
@Tag(name = "Управление входом, аутентификацией, регистрацией // perm:all")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger("Main");

    private final UserService userService;
    private final ResponseService rs;
//    private final AppUserDetails userDetails;

    public AuthController(UserService userService, ResponseService rs
//            , AppUserDetails userDetails
                          ) {
        this.userService = userService;
        this.rs=rs;
//        this.userDetails = userDetails;
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
        if(userService.existsByUsername(email)){
            return ResponseEntity.badRequest().body("email "+email+" already taken");
        }
        CreateUserRequest user = new CreateUserRequest();
        user.setRole(EUserRole.DEACTIVATED); // делаем учетку неактивированной
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
        return rs.build(userService.save(user, SAVETYPE.STANDARD_REGISTER));
    }


    @GetMapping("/check-email-availability")
    @ResponseBody
    public Map<String, Boolean> checkUsernameAvailability(@RequestParam("email") String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !userService.existsByUsername(email));
        return response;
    }
// todo для проверки springSecurity

//    @GetMapping("/check-adm-privileges")
//    public ResponseEntity<?> checkAdmPriv(){
//        return ResponseEntity.ok("У вас есть привелении администратора");
//    }
//    @GetMapping("/check-teacher-privileges")
//    public ResponseEntity<?> checkTeacherPriv(){
//        return ResponseEntity.ok("У вас есть привелении преподавателя");
//    }
//    @GetMapping("/check-dev-privileges")
//    public ResponseEntity<?> checkDevPriv(){
//        return ResponseEntity.ok("У вас есть привелении разработчика");
//    }

//    @Operation(
//            summary = "Выход из системы",
//            description = "Завершает текущую сессию пользователя",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Успешный выход"),
//                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
//            }
//    )
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout() {
//        // Этот метод теперь не нужен, так как выход обрабатывается Spring Security
//        return ResponseEntity.ok("Выход обрабатывается Spring Security");
//    }

//    @GetMapping("/login") // переадресация на страницу входа
//    public ResponseEntity<?> login(){
//        return ResponseEntity.ok("*переход на страницу входа*");
//    }
//    @Operation(summary = "Ввод entryCode !ПОСЛЕ РЕГИСТРАЦИИ!, требует логин и пароль пользователя")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Учетная запись подтверждена"),
//            @ApiResponse(responseCode = "202", description = "Пользователь уже зарегистрирован"),
//            @ApiResponse(responseCode = "300", description = "Неверный entryCode"),
//            @ApiResponse(responseCode = "301", description = "Пользователя не существует"),
//            @ApiResponse(responseCode = "303", description = "Кода не существовало. Выдан новый entryCode"),
//            @ApiResponse(responseCode = "302", description = "Неверный пароль"),
//            @ApiResponse(responseCode = "310", description = "EntryCode устарел. Выслан новый")
//    })
//    @PostMapping("/codeAfterRegister")
//    public ResponseEntity<?> getAccessToAccount(@RequestParam @NotNull String entryCode,
//                                                @RequestParam @NotNull String email,
//                                                @RequestParam @NotNull String password){
//        return userService.checkAccess(entryCode, email, password, true);
//    }

    @Operation(summary = "Получить код для входа в аккаунт")
    @PostMapping("/code/get")
    public ResponseEntity<?> getOneTimeCode(@NotNull String email){
        return userService.getOneTimeCode(email);
    }

    @Operation(summary = "Вход в аккаунт")
    @PostMapping("/login")
    public ResponseEntity<?> login(@NotNull String email, @NotNull String password, @NotNull String entryCode){
        var response = userService.loginWithOneTimeCode(email, password, entryCode);
        String token = null;
        if(response.getStatusCode() == HttpStatus.ACCEPTED){
            AppUserDetails user = new AppUserDetails(userService.getUser(null, email));
            token = userService.generateJwtToken(email);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
//        userDetails.getAuthorities().forEach(auth -> System.out.println(auth.getAuthority()));
        return token == null ? response : rs.build(token);
    }

    // проверка авторизации пользователя
    @Operation(summary = "test me")
    @GetMapping("/todelete/testme")
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

