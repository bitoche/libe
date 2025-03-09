package ru.miit.libe.dtos;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.miit.libe.models.EUserRole;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private String firstName;
    private String secondName;
    @Nullable
    private String thirdName;
    @Nullable
    private Date birthDate;
    private String email;
    private String password;
    private EUserRole role;
}
