package ru.miit.libe.dtos;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.miit.libe.models.EUserRole;

import java.sql.Date;

@Data
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

    public CreateUserRequest(String firstName, String secondName, @Nullable String thirdName, @Nullable Date birthDate, String email, String password, EUserRole role) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.thirdName = thirdName;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
