package ru.miit.libe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long userId;
    String firstName;
    String secondName;
    @Nullable
    String thirdName;
    public String getFullName(){
        return thirdName!=null? secondName+" "+firstName+" "+thirdName : secondName+" "+firstName;
    }
    Date birthDate;
    @Column(unique = true)
    String email;
    @JsonIgnore
    String password;
    EUserRole role;
    LocalDateTime registerDttm;
}
