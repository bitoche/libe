package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class BooksArriving {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long arrivingId;
    LocalDateTime arrivingDttm;
    List<String> arrivedBooksDetails;
    @ManyToMany
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.SET_NULL)
    List<OrderingBooks> orderings;
}
