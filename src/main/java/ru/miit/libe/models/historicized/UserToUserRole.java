package ru.miit.libe.models.historicized;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.miit.libe.models.EUserRole;
import ru.miit.libe.models.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class UserToUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long entryId;
    @ManyToOne
    User user;
    EUserRole role;
    LocalDateTime assignDttm;
}
