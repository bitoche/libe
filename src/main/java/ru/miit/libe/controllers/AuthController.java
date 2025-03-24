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

    public AuthController(UserService userService, ResponseService rs
                          ) {
        this.userService = userService;
        this.rs=rs;
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
        return token == null ? response : rs.build(token);
    }
}

