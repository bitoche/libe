package ru.miit.libe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import ru.miit.libe.services.RandomService;

import java.util.*;

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
//    @ManyToMany
//    @ToString.Exclude
//    @JsonIgnore
//    @OnDelete(action = OnDeleteAction.SET_NULL)
//    List<Book> authoredBooks;

    public String getFullName(){
        return thirdName!=null? secondName+" "+firstName+" "+thirdName : secondName+" "+firstName;
    }
    public String toString(){
        List<String> parts = List.of(
                identifier,
                getFullName(),
                birthDate != null ? birthDate.toString() : ""
        );
        return String.join("; \n", parts);
    }
//    public BookAuthor addBook(Book book){
//        if(authoredBooks==null){
//            authoredBooks=new ArrayList<>();
//        }
//        authoredBooks.add(book);
//        return this;
//    }
    public static BookAuthor generateMaleWOId(){
        BookAuthor gen = new BookAuthor();
        gen.firstName=RandomService.randFrom(RandomService.MALE_NAMES);
        gen.secondName=RandomService.randFrom(RandomService.MALE_SURNAMES);
        gen.thirdName=RandomService.randFrom(RandomService.MALE_PATRONYMICS);
        gen.birthDate=RandomService.generateRandomDate(100);
        gen.identifier=RandomService.generateRandomSymbols("author-",10);
        return gen;
    }
    public static BookAuthor generateFemaleWOId(){
        BookAuthor gen = new BookAuthor();
        gen.firstName=RandomService.randFrom(RandomService.FEM_NAMES);
        gen.secondName=RandomService.randFrom(RandomService.FEM_SURNAMES);
        gen.thirdName=RandomService.randFrom(RandomService.FEM_PATRONYMICS);
        gen.birthDate=RandomService.generateRandomDate(100);
        gen.identifier=RandomService.generateRandomSymbols("author-",10);
        return gen;
    }
}
