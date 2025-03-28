package ru.miit.libe.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import ru.miit.libe.services.RandomService;

import java.time.LocalDateTime;
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
    @Size(max=10000)
    String description;
    @Column(unique = true)
    String identifier;
    EBookStatus bookStatus;
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

    @Timestamp
    LocalDateTime addDttm;

    @Nullable
    @ToString.Exclude
    Boolean isActive = true; // чтобы при создании записи было сразу true

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
    public String details(){
        List<String> parts = List.of(
                bookName,
                releaseDate.toString(),
                description,
                identifier,
                publishingHouse.toString(),
                authors.toString(),
                genres.toString());
        return String.join("; ",parts);
    }

    public static Book generateWOIdAndDependencies(){
        Book gen = new Book();
        gen.identifier = RandomService.generateRandomSymbols("book-", 10);
        gen.bookName = RandomService.generateRandomPhrase();
        gen.description = RandomService.generateRandomPhrase()+"! "+RandomService.generateRandomPhrase()+".";
        gen.releaseDate = RandomService.generateRandomDate(85);
        gen.pagesNumber = RandomService.randBetween(10, 1000);
        gen.bookStatus = EBookStatus.IN_STOCK;
        gen.addDttm = LocalDateTime.now();
        return gen;
    }
}
