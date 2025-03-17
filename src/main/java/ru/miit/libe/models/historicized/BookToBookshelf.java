package ru.miit.libe.models.historicized;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import ru.miit.libe.models.Book;
import ru.miit.libe.models.Bookshelf;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class BookToBookshelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long entryId;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Book book;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Bookshelf bookshelf;
    String action;
    LocalDateTime assignDttm;

}
