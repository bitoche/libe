package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.CreateRequestBooksRequest;
import ru.miit.libe.models.EBorrowStatus;
import ru.miit.libe.models.RequestBooks;
import ru.miit.libe.services.BorrowService;
import ru.miit.libe.services.UserService;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
@RestController
@RequestMapping("/borrows")
@Tag(name = "Управление запросами на получение, бронированиями книг и т.п. // perm:librarian/student/teacher")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
@AllArgsConstructor
public class BorrowController {
    BorrowService borrowService;
    UserService userService;
    ResponseService rs;
    // бронирование книг perm:student/teacher
    @Operation(summary = "Бронирование книги пользователем, предполагаемое получение на определенную дату")
    @PostMapping("/u/book")
    public ResponseEntity<?> bookBorrow(@RequestParam long userId,
                                        @RequestParam long bookId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date expectedRecieptDate){
        return  rs.build(borrowService.borrow(userId, bookId, expectedRecieptDate));
    }
    // выдача книг perm:librarian
    @Operation(summary = "Выдать книгу по определенной брони")
    @PostMapping("/l/borrows/extradition")
    public ResponseEntity<?> extraditionBorrow(@RequestParam int borrowId,
                                               @RequestParam int redemptionPriceIfLoss,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date expectedReturnDate){
        return rs.build(borrowService.extradition(borrowId, redemptionPriceIfLoss, expectedReturnDate));
    }
    // возвращение книг perm:librarian
    @Operation(summary = "Вернуть книгу по определенной брони")
    @PostMapping("/l/borrows/return")
    public ResponseEntity<?> returnBook(@RequestParam int borrowId){
        return rs.build(borrowService.returnBook(borrowId));
    }
    // возвращение книг, книга утеряна perm:librarian
    @Operation(summary = "Книга утеряна по определенной брони")
    @PostMapping("/l/borrows/lost")
    public ResponseEntity<?> lostBook(@RequestParam int borrowId){
        return rs.build(borrowService.lostBook(borrowId));
    }
    // возвращение книг, уплачено за утерю perm:librarian
    @Operation(summary = "Утеря книги уплачена по определенной брони")
    @PostMapping("/l/borrows/paid")
    public ResponseEntity<?> paidForLostBook(@RequestParam int borrowId){
        return rs.build(borrowService.paidForLostBook(borrowId));
    }
    // получить бронь по фильтру
    @Operation(summary = "Получить бронь по пользователю")
    @GetMapping("/l/borrows/search/user")
    public ResponseEntity<?> getBorrowByUser(@RequestParam long userId){
        return rs.build(borrowService.findBorrowByUser(userId));
    }
    @Operation(summary = "Получить бронь по книге")
    @GetMapping("/l/borrows/search/book")
    public ResponseEntity<?> getBorrowByBook(@RequestParam long bookId){
        return rs.build(borrowService.findBorrowByUser(bookId));
    }
    @Operation(summary = "Получить бронь по статусу")
    @GetMapping("/l/borrows/search/status")
    public ResponseEntity<?> getBorrowByStatus(EBorrowStatus status){
        return rs.build(borrowService.findBorrowByStatus(status));
    }

    // пользователь - должен быть
    @Operation(summary = "Получить МОИ брони")
    @GetMapping("/u/borrows/get/{userId}")
    public ResponseEntity<?> getMyBorrows(@PathVariable long userId){
        //todo spring security проверка на авторизовавшегося пользователя - должен быть тот же, иначе forbidden
        return rs.build(borrowService.findBorrowByUser(userId));
    }


    // запросы книг
    @Operation(summary = "Получить все запросы книг")
    @GetMapping("/l/requests/")
    public ResponseEntity<?> getBooksRequests(){
        return rs.build(borrowService.getAllBookRequests());
    }
    @Operation(summary = "Получить все запросы книг от пользователя")
    @GetMapping("/l/requests/search/user/{userId}")
    public ResponseEntity<?> getBooksRequestsByUser(@PathVariable long userId){
        return rs.build(borrowService.getRequestBooksByUserId(userId));
    }
    // запрос книг perm:teacher - only authenticated
    @Operation(summary = "Получить все МОИ запросы книг")
    @GetMapping("/t/requests/user/{userId}")
    public ResponseEntity<?> getMyBooksRequests(@PathVariable long userId){
        //todo spring security проверка на авторизовавшегося пользователя - должен быть тот же, иначе forbidden
        return rs.build(borrowService.getRequestBooksByUserId(userId));
    }
    // запрос книг perm:teacher - only authenticated
    @Operation(summary = "Создать запрос на книги")
    @PostMapping("/t/requests/create/{userId}")
    public ResponseEntity<?> createBookRequest(@PathVariable long userId, @RequestBody CreateRequestBooksRequest cbr){
        //todo spring security проверка на авторизовавшегося пользователя - должен быть тот же, иначе forbidden
        if(userService.existsById(userId)){
            RequestBooks requestBooks = new RequestBooks();
            requestBooks.setOrderedBooks(cbr.getOrderedBooks());
            requestBooks.setRequestedUser(userService.getUser(userId, null));
            requestBooks.setRequestDttm(LocalDateTime.now());
            requestBooks.setComment(cbr.getComment());
            requestBooks.setActive(true);
            return rs.build(borrowService.saveNewRequestBooks(requestBooks));
        }
        return rs.build(null);
    }
    @Operation(summary = "Получить запросы на книги по статусу")
    @GetMapping("/l/requests/search/status")
    public ResponseEntity<?> getAllRequestsByStatus(@RequestParam boolean isActive){
        return rs.build(borrowService.getRequestBooksByStatus(isActive));
    }

    @Operation(summary = "Изменить МОЙ запрос на книги")
    @PostMapping("/t/requests/change/{userId}/{requestId}")
    public ResponseEntity<?> changeRequest(@PathVariable long userId, @PathVariable long requestId, @RequestBody CreateRequestBooksRequest createRequestBooksRequest){
        //todo spring security проверка на авторизовавшегося пользователя - должен быть тот же, иначе forbidden
        if(userService.existsById(userId)){
            var r = borrowService.getRequestBooksById(requestId);
            if(r!=null){
                if(borrowService.getRequestBooksById(requestId).isActive()){
                    r.setRequestId(requestId);
                    r.setOrderedBooks(createRequestBooksRequest.getOrderedBooks());
                    r.setComment(createRequestBooksRequest.getComment());
                    return rs.build(borrowService.updateRequestBooks(r));
                }
            }
        }
        return rs.build(null);
    }
    @Operation(summary = "Закрыть запрос на книги")
    @PutMapping("/l/requests/close/{requestId}")
    public ResponseEntity<?> getAllRequestsByStatus(@PathVariable long requestId){
        var r = borrowService.getRequestBooksById(requestId);
        if(r != null){
            r.setRequestId(requestId);
            r.setActive(false);
            rs.build(borrowService.updateStatusOfRequestBooks(r));
        }
        return rs.build("not found");
    }

    // чел забронировал но не забрал
    // это отмена брони библиотекарем

}
