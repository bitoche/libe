package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.models.*;
import ru.miit.libe.repository.*;
import ru.miit.libe.services.MainBookService;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@RequestMapping("/librarian")
@Tag(name = "Управление книгами, и т.п. // perm:librarian")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
@SecurityRequirement(name = "BearerAuth")
public class LibrarianController {
    private final MainBookService mainBookService;
    @Autowired
    private final IBookAuthorRepository bookAuthorRepository;
    @Autowired
    private final IBookGenreRepository bookGenreRepository;
    @Autowired
    private final IBookLanguageRepository bookLanguageRepository;
    @Autowired
    private final IBookShelfRepository bookShelfRepository;
    @Autowired
    private final IPublishingHouseRepository publishingHouseRepository;

    public LibrarianController(MainBookService mainBookService, IBookAuthorRepository bookAuthorRepository, IBookGenreRepository bookGenreRepository, IBookLanguageRepository bookLanguageRepository, IBookShelfRepository bookShelfRepository, IPublishingHouseRepository publishingHouseRepository) {
        this.mainBookService = mainBookService;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.bookLanguageRepository = bookLanguageRepository;
        this.bookShelfRepository = bookShelfRepository;
        this.publishingHouseRepository = publishingHouseRepository;
    }

    //add
    @Operation(summary = "Добавить новую книгу в БД")
    @PostMapping(value = "/books/create")
    @Transactional
    public ResponseEntity<?> addBook(@RequestParam String bookName,
                                     @RequestParam String bookDescription,
                                     @RequestParam int pagesNumber,
                                     @RequestParam Date releaseDate,
                                     @RequestParam String identifier,
                                     @RequestParam EBookStatus status,
                                     @RequestParam Long publishingHouseId,
                                     @RequestParam List<Long> authorIds,
                                     @RequestParam List<Integer> genreIds,
                                     @RequestParam int languageId,
                                     @RequestParam @Nullable Integer bookshelfId
                                     ){//@RequestBody @Validated Book book){
        Book book = new Book();
        book.setBookName(bookName);
        book.setDescription(bookDescription);
        book.setPagesNumber(pagesNumber);
        book.setReleaseDate(releaseDate);
        book.setIdentifier(identifier);
        book.setBookStatus(status);
        book.setAddDttm(LocalDateTime.now());
        publishingHouseRepository.findById(publishingHouseId).ifPresent(book::setPublishingHouse);
        book.setAuthors(new ArrayList<>());
        for (long authorId : authorIds){
            bookAuthorRepository.findById(authorId).ifPresent(book::addAuthor);
        }
        book.setGenres(new ArrayList<>());
        for (int genreId : genreIds){
            bookGenreRepository.findById(genreId).ifPresent(book::addGenre);
        }
        bookLanguageRepository.findById(languageId).ifPresent(book::setLanguage);
        Book createdBook = mainBookService.addBook(book);
        if (bookshelfId != null){
            var s = bookShelfRepository.findById(bookshelfId).orElse(null);
            if(s!=null){
                s.addBook(book);
                s.setShelfId(s.getShelfId());
                bookShelfRepository.save(s);
            }
        }
        return createdBook != null
                ? ResponseEntity.ok().body(createdBook)
                : ResponseEntity.badRequest().body("Такой идентификатор книги уже существует (book.identifier)");
    }
    @Operation(summary = "Обновить книгу в БД")
    @PutMapping(value = "/books/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBook(@RequestBody @Validated Book book){
        return ResponseEntity.ok().body(mainBookService.updateBook(book));
    }

    @Operation(summary = "Добавить жанр книги в БД")
    @PostMapping("/genres/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно сохранен новый жанр книги"),
            @ApiResponse(responseCode = "444", description = "Такой жанр книги уже существует (bookGenre.name)")
    })
    public ResponseEntity<?> addBookGenre(@RequestParam @NotNull String bookGenreName){
        var bg = new BookGenre();
        bg.setGenreName(bookGenreName);
        var resp = mainBookService.addBookGenre(bg);
        return resp!=null
                ? ResponseEntity.ok(bg)
                : ResponseEntity.status(444).body("Такой жанр книги уже существует (bookStatus.name)");
    }
    @Operation(summary = "Добавить издателя в БД")
    @PostMapping("/publishing-houses/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно сохранен новый издатель"),
            @ApiResponse(responseCode = "444", description = "Такой издатель уже существует (publishingHouse.name)")
    })
    public ResponseEntity<?> addPublishingHouse(@RequestParam String publishingHouseName){
        PublishingHouse publishingHouse = new PublishingHouse();
        publishingHouse.setPublishingHouseName(publishingHouseName);
        var resp = mainBookService.addPublishingHouse(publishingHouse);
        return resp!=null
                ? ResponseEntity.ok(publishingHouse)
                : ResponseEntity.status(444).body("Такой издатель уже существует (publishingHouse.name)");
    }
    @Operation(summary = "Добавить автора в БД")
    @PostMapping("/authors/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно сохранен новый автор"),
            @ApiResponse(responseCode = "444", description = "Такой автор уже существует (bookAuthor.identifier)")
    })
    public ResponseEntity<?> addBookAuthor(@RequestParam String author_identifier,
                                           @RequestParam String firstName,
                                           @RequestParam String secondName,
                                           @RequestParam @Nullable String thirdName,
                                           @RequestParam @Nullable Date birthDate){
        BookAuthor bookAuthor = new BookAuthor();
        bookAuthor.setIdentifier(author_identifier);
        bookAuthor.setFirstName(firstName);
        bookAuthor.setSecondName(secondName);
        if(thirdName!=null && !thirdName.isEmpty()){
            bookAuthor.setThirdName(thirdName);
        }
        if(birthDate!=null){
            bookAuthor.setBirthDate(birthDate);
        }
        var resp = mainBookService.addBookAuthor(bookAuthor);
        return resp!=null
                ? ResponseEntity.ok(bookAuthor)
                : ResponseEntity.status(444).body("Такой автор уже существует (bookAuthor.identifier)");
    }
    @Operation(summary = "Добавить язык книги в БД")
    @PostMapping("/languages/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно сохранен новый язык книги"),
            @ApiResponse(responseCode = "444", description = "Такой язык уже существует (bookLanguage.language_name)")
    })
    public ResponseEntity<?> addBookLanguage(@RequestParam String languageName){
        BookLanguage bookLanguage = new BookLanguage();
        bookLanguage.setLanguageName(languageName);
        var resp = mainBookService.addBookLanguage(bookLanguage);
        return resp!=null
                ? ResponseEntity.ok(bookLanguage)
                : ResponseEntity.status(444).body("language "+languageName+" already exists");
    }
    @DeleteMapping("/books/delete")
    @Operation(summary = "Удаляет книгу (помечает удаленной)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга успешно удалена"),
            @ApiResponse(responseCode = "444", description = "Такой книги не существует")
    })
    public ResponseEntity<?> deleteBookById(@RequestParam Long id){
        return ResponseEntity.ok().body(mainBookService.deleteBookById(id));
    }

    // выдача книг perm:librarian
}
