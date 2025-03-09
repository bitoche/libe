package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class RequestBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long requestId;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    User requestedUser;
    @ManyToMany
    @ToString.Exclude
    List<InOrderBook> orderedBooks;
    boolean isActive;
    @Nullable
    String comment;
    LocalDateTime requestDttm;
}
