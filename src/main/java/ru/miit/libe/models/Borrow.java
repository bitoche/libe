package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long borrowId;
    // при бронировании
    LocalDateTime borrowDttm;
    // при бронировании
    Date expectedRecieptDate;
    // при получении книги
    @Nullable
    LocalDateTime factRecieptDttm;
    // при бронировании
    Date expectedReturnDate;
    // заполняется при сдаче книги назад
    @Nullable
    LocalDateTime factReturnDttm;
    // при бронировании
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    User borrowedUser;
    // при бронировании
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Book borrowedBook;
    // при получении книги
    @Nullable
    Integer redemptionPriceIfLoss;
    @Nullable
    LocalDateTime paidDttm;
    // в каждом из случаев
    EBorrowStatus borrowStatus;
}
