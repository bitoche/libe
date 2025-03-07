package ru.miit.libe.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name="book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long bookId;
    String bookName;
    Date releaseDate;
    int pagesNumber;
    String description;
    @Column(unique = true)
    String identifier;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    BookStatus bookStatus;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    PublishingHouse publishingHouse;
    @ManyToMany
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.SET_NULL)
    List<BookAuthor> authors;
    @ManyToMany
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.SET_NULL)
    List<BookGenre> genres;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    BookLanguage language;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Bookshelf bookshelf;


    public void addAuthor(BookAuthor a){
        if(authors==null){
            authors=new ArrayList<>();
        }
        authors.add(a);
    }

    public void addGenre(BookGenre g){
        if(genres==null){
            genres=new ArrayList<>();
        }
        genres.add(g);
    }
}
