package ru.miit.libe.controllers;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.models.Book;
import ru.miit.libe.models.BookAuthor;
import ru.miit.libe.models.BookGenre;
import ru.miit.libe.models.PublishingHouse;
import ru.miit.libe.repository.IBookRepository;
import ru.miit.libe.services.MainBookService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RestController
@RequestMapping("/api/book")
@Tag(name = "Юзер-контроллер книг // perm:all", description = "Позволяет найти нужную книгу по автору, идентификатору, и т.д.")
@CrossOrigin("http://localhost:3000/")
public class BookController {
    @Autowired
    private final IBookRepository bookRepository;
    private final MainBookService mainBookService;
    private final ResponseService rs;

    public BookController(IBookRepository bookRepository, MainBookService mainBookService, ResponseService rs) {
        this.bookRepository = bookRepository;
        this.mainBookService = mainBookService;
        this.rs=rs;
    }

    @GetMapping("/byName")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Книги успешно найдены"),
//            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
//    })
    public ResponseEntity<?> getBooksByName(@RequestParam String namePart){
        return rs.build(bookRepository.findAllByBookNameContains(namePart));
    }

    @GetMapping("/byAuthorIdentifier")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Книги успешно найдены"),
//            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
//    })
    public ResponseEntity<?> getBooksByAuthor(@RequestParam String authorIdentifier){
        return rs.build(bookRepository.findAllByAuthors_IdentifierIn(Collections.singleton(authorIdentifier))) ;
    }
    @GetMapping("/byBookIdentifier")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Книга успешно найдена"),
//            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
//    })
    public ResponseEntity<?> getBooksByIdentifier(@RequestParam String fullIdentifier){
        return rs.build(bookRepository.findByIdentifier(fullIdentifier));
    }

    @GetMapping("/byBookGenre")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Книга успешно найдена"),
//            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
//    })
    public ResponseEntity<?> getBooksByGenre(@RequestParam String genreName){
        return rs.build(bookRepository.findAllByGenres_GenreNameIn(Collections.singleton(genreName))) ;
    }

//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Успешно получен список книг"),
//            @ApiResponse(responseCode = "201", description = "Не найдено ни одной книги")
//    })
    @Operation(summary = "Позволяет получить список книг из БД")
    @GetMapping("/getBooks")
    public ResponseEntity<?> getAllBooks(@Nullable boolean showOnlyCount){
        var r = mainBookService.getAllBooks();
        return showOnlyCount ? rs.build((long) r.size()) : rs.build(r);
    }
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Успешное получен список книг"),
//            @ApiResponse(responseCode = "201", description = "Не найдено ни одной книги")
//    })
    @Operation(summary = "Позволяет получить все книги, подходящие под запрос.",
    description = "Ищет по частям запроса подходящие книги по авторам, описанию, названию, и т.д. Может быть улучшен")
    @GetMapping("/search")
    public ResponseEntity<?> getBooksBySearchRequest(@RequestParam @NotNull @Size(min = 3) String searchRequest, @Nullable boolean showOnlyCount){
        var r = mainBookService.searchBooksFromSearchField(searchRequest);
        return showOnlyCount ? rs.build((long) r.size()) : rs.build(r);
    }



}
