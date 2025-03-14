package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Deprecated
public class UserRole implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int roleId;
    String roleName;
    @Override
    public String getAuthority(){
        return "ROLE_"+getRoleName();
    }
//    @OneToMany
//    @ToString.Exclude
//    //@OnDelete(action = OnDeleteAction.SET_NULL)
//    List<User> usersWithThisRole;
}
