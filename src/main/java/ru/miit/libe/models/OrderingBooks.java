package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class OrderingBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long orderingId;
    LocalDateTime orderingDttm;
    Date expectedArrivalDate;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    User orderedAdministrator;
    @ManyToMany
    @ToString.Exclude
    List<InOrderBook> orderedBooks;
    boolean isActive;
    @Nullable
    String comment;
}
