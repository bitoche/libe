package ru.miit.libe.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name="book_author")
public class BookAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long authorId;
    @Column(unique = true)
    String identifier;
    String firstName;
    String secondName;
    @Nullable
    String thirdName;
    @Nullable
    Date birthDate;
    @ManyToMany
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.SET_NULL)
    List<Book> authoredBooks;

    public String getFullName(){
        return thirdName!=null? secondName+" "+firstName+" "+thirdName : secondName+" "+firstName;
    }
}
