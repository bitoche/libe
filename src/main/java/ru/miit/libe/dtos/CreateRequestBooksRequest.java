package ru.miit.libe.dtos;

import lombok.Getter;
import lombok.Setter;
import ru.miit.libe.models.InOrderBook;
import ru.miit.libe.models.User;

import java.util.List;

@Getter
@Setter
public class CreateRequestBooksRequest {
    List<InOrderBook> orderedBooks;
    String comment;
}
