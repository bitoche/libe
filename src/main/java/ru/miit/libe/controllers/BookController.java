package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "Юзер-контроллер книг", description = "Позволяет найти нужную книгу по автору, идентификатору, и т.д.")
@CrossOrigin("http://localhost:3000/")
public class BookController {
    @Autowired
    private final IBookRepository bookRepository;
    private final MainBookService mainBookService;

    public BookController(IBookRepository bookRepository, MainBookService mainBookService) {
        this.bookRepository = bookRepository;
        this.mainBookService = mainBookService;
    }
//    private ResponseEntity<?> bookRespToBookDTO(List<Book> books){
//        if (books!=null && !books.isEmpty()){
//            List<BookDTO> resp1 = new ArrayList<>();
//            for (Book b : books){
//                resp1.add(new BookDTO(b));
//            }
//            return ResponseEntity.status(200).body(resp1);
//        }
//        return ResponseEntity.status(201).build();
//    }
//    private MinBookDTO convertToDTO(Book book) {
//        MinBookDTO dto = new MinBookDTO();
//        dto.setBookId(book.getBookId());
//        dto.setBookName(book.getBookName());
//        dto.setReleaseDate(book.getReleaseDate());
//        dto.setPagesNumber(book.getPagesNumber());
//        dto.setDescription(book.getDescription());
//        dto.setIdentifier(book.getIdentifier());
//
//        dto.setAuthorNames(book.getAuthors().stream()
//                .map(BookAuthor::getFullName)
//                .collect(Collectors.toSet()));
//
//        dto.setGenreNames(book.getGenres().stream()
//                .map(BookGenre::getGenreName)
//                .collect(Collectors.toSet()));
//
//        dto.setPublishingHouseNames(book.getPublishingHouses().stream()
//                .map(PublishingHouse::getPublishingHouseName)
//                .collect(Collectors.toSet()));
//        if (book.getLanguage() != null){
//            dto.setLanguageName(book.getLanguage().getLanguageName());
//        }
//        if(book.getBookStatus() != null){
//            dto.setBookStatusName(book.getBookStatus().getStatusName());
//        }
//        return dto;
//    }

    @GetMapping("/byName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книги успешно найдены"),
            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
    })
    public List<Book> getBooksByName(@RequestParam String namePart){
        return bookRepository.findAllByBookNameContains(namePart);
    }

    @GetMapping("/byAuthorIdentifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книги успешно найдены"),
            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
    })
    public List<Book> getBooksByAuthor(@RequestParam String authorIdentifier){
        return bookRepository.findAllByAuthors_IdentifierIn(Collections.singleton(authorIdentifier));
    }
    @GetMapping("/byBookIdentifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга успешно найдена"),
            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
    })
    public ResponseEntity<?> getBooksByIdentifier(@RequestParam String fullIdentifier){
        var resp = bookRepository.findByIdentifier(fullIdentifier);
        return resp!=null
                ? ResponseEntity.status(200).body(resp)
                : ResponseEntity.status(201).build();
    }

    @GetMapping("/byBookGenre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга успешно найдена"),
            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
    })
    public List<Book> getBooksByGenre(@RequestParam String genreName){
        //var resp = bookRepository.findAllByGenres_GenreNameIn(genreName);
        return bookRepository.findAllByGenres_GenreNameIn(Collections.singleton(genreName));
    }

//    @GetMapping("/byBookReleaseYear/{releaseYear}")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Книги успешно найдены"),
//            @ApiResponse(responseCode = "201", description = "Запрос выполнен, книг не найдено")
//    })
//    public ResponseEntity<?> getBooksByReleaseYear(@PathVariable int releaseYear){
//        var resp = bookRepository.findBooksByReleaseYearNative(releaseYear);
//        return bookRespToBookDTO(resp);
//    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен список книг"),
            @ApiResponse(responseCode = "201", description = "Не найдено ни одной книги")
    })
    @Operation(summary = "Позволяет получить список книг из БД")
    @GetMapping("/getBooks")
    public ResponseEntity<?> getAllBooks(){
        List<Book> books = mainBookService.getAllBooks();
//        List<MinBookDTO> bookDTOs = books.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
        return ResponseEntity.ok(books);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получен список книг"),
            @ApiResponse(responseCode = "201", description = "Не найдено ни одной книги")
    })
    @Operation(summary = "Позволяет книг (по полю название, год выпуска, идентификатор книги, идентификатор автора, и жанр)")
    @GetMapping("/search")
    public ResponseEntity<?> getBooksBySearchRequest(@RequestParam @NotNull String searchRequest){
        var resp = mainBookService.searchBooksFromSearchField(searchRequest);
        return ResponseEntity.ok().body(resp); //bookRespToBookDTO(resp);
    }

}
