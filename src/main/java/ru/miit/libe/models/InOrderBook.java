package ru.miit.libe.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class InOrderBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long bookId;
    String bookName;
    Date releaseDate;
    String bookAuthor;
    String publishingHouse;
    @Size(max=1000)
    String comment;
    boolean isRecieved;
}
