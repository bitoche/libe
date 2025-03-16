package ru.miit.libe.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;
import ru.miit.libe.models.EUserRole;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotNull
    Long userId;
    @Nullable
    String newFirstName;
    @Nullable
    String newSecondName;
    @Nullable
    String newThirdName;
    @Nullable
    Date newBirthDate;
    @Nullable
    String newPassword;
    @Nullable
    EUserRole newRole;
}
