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
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long borrowId;
    LocalDateTime borrowDttm;
    Date expectedReturnDate;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    User borrowedUser;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Book borrowedBook;
    @Nullable
    Integer redemptionPriceIfLoss;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    BorrowStatus borrowStatus;
}
