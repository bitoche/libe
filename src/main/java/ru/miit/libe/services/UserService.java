package ru.miit.libe.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.miit.libe.dtos.CreateUserRequest;
import ru.miit.libe.dtos.MailMessageDTO;
import ru.miit.libe.dtos.UpdateUserRequest;
import ru.miit.libe.models.*;
import ru.miit.libe.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IEntryCodeRepository entryCodeRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<EUserRole> getAllUserRoles() {
        return List.of(EUserRole.values());
    }

    public EUserRole getUserRoleByRoleName(String roleName) {
        return EUserRole.parseString(roleName);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public ResponseEntity<?> checkAccess(String entryCode, String email, String password, boolean isRegister) {
        var currUser = userRepository.findAppUserByEmail(email);
        if (currUser.isPresent() && entryCodeRepository.existsByUser(currUser.get())) {
            var currEntryCode = entryCodeRepository.getEntryCodeByUser_UserId(currUser.get().getUserId());
            if (currEntryCode.getExpireDateTime().isBefore(LocalDateTime.now())) {
                var newCode = new EntryCode(currUser.get());
                createEntryCode(newCode);
                sendEnterCodeToEmail(newCode, isRegister);
                return ResponseEntity.status(310).build();
            }
            if (Objects.equals(entryCode, currEntryCode.getCode())) {
                PasswordEncoder pe = new BCryptPasswordEncoder();
                if (pe.matches(password, currUser.get().getPassword())) {
                    entryCodeRepository.delete(currEntryCode);
                    var enteredUser = userRepository.getUserByUserId(currUser.get().getUserId());
                    // меняю роль пользователя на "AUTHORIZED"
                    enteredUser.setRole(EUserRole.AUTHORIZED);
                    userRepository.save(enteredUser);
                    return ResponseEntity.status(200).body(enteredUser);
                } else {
                    return ResponseEntity.status(302).build();
                }
            } else {
                return ResponseEntity.status(300).build();
            }
        } else {
            if (currUser.isPresent()) {
                if (currUser.get().getRole().equals(EUserRole.AUTHORIZED)) {
                    // если пользователь уже активирован возвращаем 202 - ок
                    return ResponseEntity.status(202).build();
                }
                var newCode = new EntryCode(currUser.get());
                createEntryCode(newCode);
                sendEnterCodeToEmail(newCode, isRegister);
                return ResponseEntity.status(303).build();
            }
            return ResponseEntity.status(301).build();
        }
    }

    public void sendEnterCodeToEmail(EntryCode ec, boolean isRegister){
        try{
            var ecmm = new MailMessageDTO(ec, isRegister);
            emailService.sendEmail(ecmm.getSender(), ecmm.getReceiver(), ecmm.getMailTitle(), ecmm.getTextMessage());
        }
        catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }
    public EntryCode createEntryCode(EntryCode entryCode){
        var user = entryCode.getUser();
        if(user!=null && userRepository.existsByEmail(user.getEmail())){
            entryCodeRepository.save(entryCode);
            return entryCode;
        }
        return null;
    }

    public User save(CreateUserRequest user, SAVETYPE saveType) {
        var obj = new User();
        obj.setEmail(user.getEmail());
        obj.setPassword(user.getPassword());
        obj.setBirthDate(user.getBirthDate());
        obj.setSecondName(user.getSecondName());
        obj.setFirstName(user.getFirstName());
        obj.setThirdName(user.getThirdName());
        obj.setEmail(user.getEmail());
        obj.setRegisterDttm(LocalDateTime.now());
        if(!checkUserNotExists(user.getEmail(), null)){
            return null;
        }

        if (saveType == SAVETYPE.WITH_ROLE_INCLUDED) {
            // присваиваем переданную роль
            obj.setRole(user.getRole());
        } else {
            // присваиваем deactivated
            obj.setRole(EUserRole.DEACTIVATED);
        }
        PasswordEncoder pe = new BCryptPasswordEncoder();
        obj.setPassword(pe.encode(obj.getPassword()));
        obj = userRepository.save(obj);
        if (saveType == SAVETYPE.WITH_ROLE_INCLUDED){
            // отправлять код или нет при регистрации админом?
            // сейчас - не отправлять
        }
        else{
            EntryCode firstEntryCode = new EntryCode(obj);
            createEntryCode(firstEntryCode);
            sendEnterCodeToEmail(firstEntryCode, true);
        }
        return obj;
    }

    public User update(UpdateUserRequest obj) {
        if (checkUserNotExists(null,obj.getUserId())){
            return null;
        }
        var user = getUser(obj.getUserId(), null);
        if(obj.getNewFirstName()!=null){
            user.setFirstName(obj.getNewFirstName());
        }
        if(obj.getNewSecondName()!=null){
            user.setSecondName(obj.getNewSecondName());
        }
        if(obj.getNewThirdName()!=null){
            user.setThirdName(obj.getNewThirdName());
        }
        if(obj.getNewBirthDate()!=null){
            user.setBirthDate(obj.getNewBirthDate());
        }
        if(obj.getNewPassword()!=null){
            PasswordEncoder pe = new BCryptPasswordEncoder();
            user.setPassword(pe.encode(obj.getNewPassword()));
        }
        if(obj.getNewRole()!=null){
            user.setRole(obj.getNewRole());
        }
        userRepository.save(user);
        return user;
    }

    public boolean addRoleUpdate(User user) {
        if (userRepository.findAppUserByEmail(user.getEmail()).isEmpty()) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    public User deleteUserById(Long id) {
        // Находим пользователя
        var deletedUser = userRepository.findById(id);
        if(deletedUser.isPresent()){
            userRepository.deleteById(id);
            return deletedUser.get();
        }
        return null;
    }

    public boolean existsByUsername(String email) {
        return userRepository.findAppUserByEmail(email).isPresent();
    }
    public boolean existsById(long id){ return userRepository.existsById(id);}
    public User getUserByUsername(String email) {
        return userRepository.findAppUserByEmail(email).orElse(null);
    }

    public ResponseEntity<?> getOneTimeCode(String email){
        // Проверяем, существует ли пользователь
        if(checkUserNotExists(email, null)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("user not found");
        }
        User user = getUser(null, email);
        // код есть, но истек
        if (entryCodeRepository.existsByUser(user) && entryCodeRepository.getEntryCodeByUser_UserId(user.getUserId()).getExpireDateTime().isBefore(LocalDateTime.now())){
            entryCodeRepository.delete(entryCodeRepository.getEntryCodeByUser_UserId(user.getUserId())); // удалили старый код
        }
        // стандартный поток
        if (!entryCodeRepository.existsByUser(user)){
            var ec = new EntryCode(user);
            createEntryCode(ec); // использовать только этот метод, т.к. имя user зарезервировано в postgre
            sendEnterCodeToEmail(ec,false);
            return ResponseEntity.ok().body("Успешно отправлен одноразовый код");
        }
        return ResponseEntity.ok().body("Одноразовый код уже был отправлен");
    }
    public boolean checkUserNotExists(@Nullable String email, @Nullable Long userId){
        Optional<User> userOptional = Optional.empty();
        if (userId != null){
            userOptional = userRepository.findById(userId);
        }
        else if (email != null){
            userOptional = userRepository.findAppUserByEmail(email);
        }
        return userOptional.isEmpty();
    }

    public User getUser(@Nullable Long userId, @Nullable String email){
        Optional<User> userOptional = Optional.empty();
        if (userId != null){
            userOptional = userRepository.findById(userId);
        }
        else if (email != null){
            userOptional = userRepository.findAppUserByEmail(email);
        }
        return userOptional.orElseThrow();
    }
    public boolean equalPassword(User user, String password){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, user.getPassword());
    }

    public String checkEntryCode(User user, String code){
        // codes:
        // 1 - ok,
        // 2 - entry code dont exists,
        // 3 - entry code expired
        EntryCode entryCode = entryCodeRepository.findByUserAndCode(user, code);
        if (entryCode == null){
            return "entry code doesn't exists";
        }
        if (entryCode.getExpireDateTime().isBefore(LocalDateTime.now())){
            return "entry code expired";
        }
        return "ok";
    }

    public ResponseEntity<?> loginWithOneTimeCode(String email, String password, String code) {
        // Проверяем, существует ли пользователь
        if(checkUserNotExists(email, null)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("user not found");
        }
        User user = getUser(null, email);
        // проверка подтверждена ли учетка
        if (user.getRole() == EUserRole.DEACTIVATED){
            // если нет - подтверждаем
            return checkAccess(code,email,password,true);
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user "+user.getUserId()+" deactivated");
        }
        // Проверяем пароль
        if (!equalPassword(user, password)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("password not equals");
        }

        // Проверяем код
        EntryCode entryCode = entryCodeRepository.findByUserAndCode(user, code);
        var ec_check = checkEntryCode(user, code);
        if (!Objects.equals(ec_check, "ok")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ec_check);
        };
        // Удаляем использованный код
        entryCodeRepository.delete(entryCode);

        // Возвращаем успешный ответ
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }
}