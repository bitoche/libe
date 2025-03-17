package ru.miit.libe.models.historicized;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import ru.miit.libe.models.Book;
import ru.miit.libe.models.EBookStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class BookToBookStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long entryId;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Book book;
    EBookStatus status;
    LocalDateTime assignDttm;
}
