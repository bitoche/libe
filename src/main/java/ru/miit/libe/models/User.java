package ru.miit.libe.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

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
    String password;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    UserRole role;
}
