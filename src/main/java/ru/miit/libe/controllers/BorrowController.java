package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.miit.libe.services.BorrowService;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
@RestController
@RequestMapping("/api/borrow")
@Tag(name = "Управление запросами на получение, бронированиями книг и т.п. // perm:librarian/student/teacher")
@CrossOrigin("http://localhost:3000/")
public class BorrowController {
    BorrowService borrowService;
    ResponseService rs;
    // бронирование книг perm:student/teacher
    @Operation(summary = "Бронирование книги пользователем, предполагаемое получение на определенную дату")
    @PostMapping("/bookBorrow")
    public ResponseEntity<?> bookBorrow(long userId, long bookId, Date expectedRecieptDate){
        return  rs.build(borrowService.borrow(userId, bookId, expectedRecieptDate));
    }
    // выдача книг perm:librarian
    @Operation(summary = "Выдать книгу по определенной брони")
    @PostMapping("/l/extradition")
    public ResponseEntity<?> extraditionBorrow(int borrowId, @Nullable LocalDateTime receptDttm, int redemptionPriceIfLoss, Date expectedReturnDate){
        return rs.build(borrowService.extradition(borrowId, receptDttm, redemptionPriceIfLoss, expectedReturnDate));
    }
    // запрос книг perm:teacher

}
