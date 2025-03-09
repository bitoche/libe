package ru.miit.libe.dtos;
import lombok.*;
import ru.miit.libe.models.InOrderBook;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderBooksRequest {
    private Date expectedArrivalDate;
    private long orderedLibrarianId;
    private List<InOrderBook> orderedBooks;
    private String comment;
}
