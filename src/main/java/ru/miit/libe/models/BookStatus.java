package ru.miit.libe.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name="book_status")
@Deprecated
public class BookStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int bookStatusId;
    String statusName;
//    @ManyToMany
//    @ToString.Exclude
//    @Nullable
//    @JsonIgnore
//    @OnDelete(action = OnDeleteAction.SET_NULL)
//    List<Book> booksWithStatus;
//
//    public BookStatus addBook(Book book){
//        if(booksWithStatus==null){
//            booksWithStatus=new ArrayList<>();
//        }
//        booksWithStatus.add(book);
//        return this;
//    }
}
