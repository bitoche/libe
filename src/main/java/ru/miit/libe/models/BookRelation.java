package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name="book_relation")
public class BookRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int relationId;
    String relationName;
    @OneToMany
    @ToString.Exclude
    //@OnDelete(action = OnDeleteAction.SET_NULL)
    List<BookToBookRelation> bookToBookRelations;
}
