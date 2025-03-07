package ru.miit.libe.models;

import jakarta.annotation.Nullable;
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
@Table(name="bookshelf")
public class Bookshelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int shelfId;
    String shelfName;
    @ManyToOne
    Cabinet cabinet;
    @Nullable
    @OneToMany
    @ToString.Exclude
    //@OnDelete(action = OnDeleteAction.SET_NULL)
    List<Book> books;
}
