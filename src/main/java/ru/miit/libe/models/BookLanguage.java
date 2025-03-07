package ru.miit.libe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name="book_language")
public class BookLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int languageId;
    String languageName;
//    @ManyToMany
//    @ToString.Exclude
//    @JsonIgnore
//    @OnDelete(action = OnDeleteAction.SET_NULL)
//    List<Book> booksWithThisLanguage;
//
//    public BookLanguage addBook(Book book){
//        if(booksWithThisLanguage==null){
//            booksWithThisLanguage=new ArrayList<>();
//        }
//        booksWithThisLanguage.add(book);
//        return this;
//    }
}
