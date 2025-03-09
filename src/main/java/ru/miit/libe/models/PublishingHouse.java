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
@Table(name="publishing_house")
public class PublishingHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long publishingHouseId;
    String publishingHouseName;
    public String toString(){
        return publishingHouseName;
    }
//    @ManyToMany
//    @ToString.Exclude
//    @JsonIgnore
//    @OnDelete(action = OnDeleteAction.SET_NULL)
//    List<Book> publishedBooks;

//    public PublishingHouse addBook(Book book){
//        if(publishedBooks==null){
//            publishedBooks=new ArrayList<>();
//        }
//        publishedBooks.add(book);
//        return this;
//    }
}
