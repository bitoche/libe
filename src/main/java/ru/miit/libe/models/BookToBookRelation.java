package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name="book_to_book_relation")
public class BookToBookRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int relationId;
    String relationDescription;
    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Book mainBook;
    @NotNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Book relatedBook;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    BookRelation relation;
}
