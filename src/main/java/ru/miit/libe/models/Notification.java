package ru.miit.libe.models;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long notificationId;
    @NotNull
    String title;
    @Nullable
    String text;
    @NotNull
    @Timestamp
    LocalDateTime notificationDttm;
    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.SET_NULL)
    User user;
    boolean isChecked;
}
