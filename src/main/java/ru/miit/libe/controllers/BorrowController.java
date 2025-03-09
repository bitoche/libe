package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.models.EBorrowStatus;
import ru.miit.libe.services.BorrowService;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
@RestController
@RequestMapping("/api/borrow")
@Tag(name = "Управление запросами на получение, бронированиями книг и т.п. // perm:librarian/student/teacher")
@CrossOrigin("http://localhost:3000/")
@AllArgsConstructor
public class BorrowController {
    BorrowService borrowService;
    ResponseService rs;
    // бронирование книг perm:student/teacher
    @Operation(summary = "Бронирование книги пользователем, предполагаемое получение на определенную дату")
    @PostMapping("/bookBorrow")
    public ResponseEntity<?> bookBorrow(long userId, long bookId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date expectedRecieptDate){
        return  rs.build(borrowService.borrow(userId, bookId, expectedRecieptDate));
    }
    // выдача книг perm:librarian
    @Operation(summary = "Выдать книгу по определенной брони")
    @PostMapping("/l/extradition")
    public ResponseEntity<?> extraditionBorrow(int borrowId, int redemptionPriceIfLoss, @DateTimeFormat(pattern = "yyyy-MM-dd") Date expectedReturnDate){
        return rs.build(borrowService.extradition(borrowId, redemptionPriceIfLoss, expectedReturnDate));
    }
    // возвращение книг perm:librarian
    @Operation(summary = "Вернуть книгу по определенной брони")
    @PostMapping("/l/return")
    public ResponseEntity<?> returnBook(int borrowId){
        return rs.build(borrowService.returnBook(borrowId));
    }
    // возвращение книг, книга утеряна perm:librarian
    @Operation(summary = "Книга утеряна по определенной брони")
    @PostMapping("/l/lost")
    public ResponseEntity<?> lostBook(int borrowId){
        return rs.build(borrowService.lostBook(borrowId));
    }
    // возвращение книг, уплачено за утерю perm:librarian
    @Operation(summary = "Утеря книги уплачена по определенной брони")
    @PostMapping("/l/paidForLostBook")
    public ResponseEntity<?> paidForLostBook(int borrowId){
        return rs.build(borrowService.paidForLostBook(borrowId));
    }
    // получить бронь по фильтру
    @Operation(summary = "Получить бронь по пользователю")
    @GetMapping("/l/getBorrowByUser")
    public ResponseEntity<?> getBorrowByUser(long userId){
        return rs.build(borrowService.findBorrowByUser(userId));
    }
    @Operation(summary = "Получить бронь по книге")
    @GetMapping("/l/getBorrowByBook")
    public ResponseEntity<?> getBorrowByBook(long bookId){
        return rs.build(borrowService.findBorrowByUser(bookId));
    }
    @Operation(summary = "Получить бронь по статусу")
    @GetMapping("/l/getBorrowByStatus")
    public ResponseEntity<?> getBorrowByStatus(EBorrowStatus status){
        return rs.build(borrowService.findBorrowByStatus(status));
    }
    // запрос книг perm:teacher

}
