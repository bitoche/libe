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
@Table(name="book_genre")
public class BookGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int genreId;
    String genreName;
    public String toString(){
        return genreName;
    }
//    @ManyToMany
//    @ToString.Exclude
//    @JsonIgnore
//    @OnDelete(action = OnDeleteAction.SET_NULL)
//    List<Book> booksWithGenre;
//
//    public BookGenre addBook(Book book){
//        if(booksWithGenre==null){
//            booksWithGenre=new ArrayList<>();
//        }
//        booksWithGenre.add(book);
//        return this;
//    }
}
