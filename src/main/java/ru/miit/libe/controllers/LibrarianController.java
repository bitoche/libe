package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@RequestMapping("/api/adm")
@Tag(name = "Управление книгами, и т.п.")
@CrossOrigin("http://localhost:3000/")
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
    @Autowired
    private final IBookStatusRepository bookStatusRepository;
    public LibrarianController(MainBookService mainBookService, IBookAuthorRepository bookAuthorRepository, IBookGenreRepository bookGenreRepository, IBookLanguageRepository bookLanguageRepository, IBookShelfRepository bookShelfRepository, IPublishingHouseRepository publishingHouseRepository, IBookStatusRepository bookStatusRepository) {
        this.mainBookService = mainBookService;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.bookLanguageRepository = bookLanguageRepository;
        this.bookShelfRepository = bookShelfRepository;
        this.publishingHouseRepository = publishingHouseRepository;
        this.bookStatusRepository = bookStatusRepository;
    }
    //getall

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получен список статусов книг"),
            @ApiResponse(responseCode = "499", description = "Не найдено ни одного статуса книги")
    })
    @Operation(summary = "Позволяет получить список статусов книг из БД")
    @GetMapping("/getBookStatuses")
    public ResponseEntity<?> getAllBookStatuses(){
        var allBookStatuses = mainBookService.getAllBookStatuses();
        return allBookStatuses !=null
                ? ResponseEntity.ok(allBookStatuses)
                : ResponseEntity.status(499).body("Не найдено ни одного статуса книги");
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получен список жанров книг"),
            @ApiResponse(responseCode = "499", description = "Не найдено ни одного жанра книги")
    })
    @Operation(summary = "Позволяет получить список жанров книг из БД")
    @GetMapping("/getBookGenres")
    public ResponseEntity<?> getAllBookGenres(){
        var allBookGenres = mainBookService.getAllBookGenres();
        return allBookGenres !=null
                ? ResponseEntity.ok(allBookGenres)
                : ResponseEntity.status(499).body("Не найдено ни одного жанра книги");
    }
    @Operation(summary = "Позволяет получить всех издателей из БД")
    @GetMapping("/getPublishingHouses")
    public ResponseEntity<?> getPublishingHouses(){
        var allPublishingHouses = mainBookService.getAllPublishingHouses();
        return allPublishingHouses !=null
                ? ResponseEntity.ok(allPublishingHouses)
                : ResponseEntity.status(499).body("Не найдено ни одного издательства");
    }
    @Operation(summary = "Позволяет получить всех авторов из БД")
    @GetMapping("/getBookAuthors")
    public ResponseEntity<?> getBookAuthors(){
        var allBookAuthors = mainBookService.getAllBookAuthors();
        return allBookAuthors !=null
                ? ResponseEntity.ok(allBookAuthors)
                : ResponseEntity.status(499).body("Не найдено ни одного автора");
    }
    @Operation(summary = "Позволяет получить все языки из БД")
    @GetMapping("/getBookLanguages")
    public ResponseEntity<?> getBookLanguages(){
        var allBookLanguages = mainBookService.getAllBookLanguages();
        return allBookLanguages !=null
                ? ResponseEntity.ok(allBookLanguages)
                : ResponseEntity.status(499).body("Не найдено ни одного языка");
    }


    //add
    @Operation(summary = "Добавить новую книгу в БД")
    @PostMapping(value = "/addBook")
    @Transactional
    public ResponseEntity<?> addBook(@RequestParam String bookName,
                                     @RequestParam String bookDescription,
                                     @RequestParam int pagesNumber,
                                     @RequestParam Date releaseDate,
                                     @RequestParam String identifier,
                                     @RequestParam int statusId,
                                     @RequestParam Long publishingHouseId,
                                     @RequestParam List<Long> authorIds,
                                     @RequestParam List<Integer> genreIds,
                                     @RequestParam int languageId,
                                     @RequestParam @Nullable Integer bookshelfId){//@RequestBody @Validated Book book){
        Book book = new Book();
        book.setBookName(bookName);
        book.setDescription(bookDescription);
        book.setPagesNumber(pagesNumber);
        book.setReleaseDate(releaseDate);
        book.setIdentifier(identifier);
        bookStatusRepository.findByBookStatusId(statusId).ifPresent(book::setBookStatus);
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
        if (bookshelfId != null){
            bookShelfRepository.findById(bookshelfId).ifPresent(book::setBookshelf);
        }
        Book createdBook = mainBookService.addBook(book);

//        // обновляем связи
//        for (long authorId : authorIds){
//            bookAuthorRepository.findById(authorId)
//                    .ifPresent(bookAuthor -> bookAuthorRepository
//                                    .save(bookAuthor.addBook(createdBook)));
//        }
//        for (int genreId : genreIds){
//            bookGenreRepository.findById(genreId).ifPresent(
//                    bookGenre -> bookGenreRepository
//                                    .save(bookGenre.addBook(createdBook)));
//        }
//        publishingHouseRepository.findById(publishingHouseId).ifPresent(
//                ph -> publishingHouseRepository
//                                    .save(ph.addBook(createdBook)));
//        bookStatusRepository.findById(statusId).ifPresent(
//                status -> bookStatusRepository
//                                    .save(status.addBook(createdBook)));
//        bookLanguageRepository.findById(statusId).ifPresent(
//                lang -> bookLanguageRepository
//                        .save(lang.addBook(createdBook)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    @Operation(summary = "Обновить книгу в БД")
    @PutMapping(value = "/updateBook", consumes = MediaType.APPLICATION_JSON_VALUE)
    // todo переделать на поля вместо сущности
    public ResponseEntity<?> updateBook(@RequestBody @Validated Book book){
//        book.setBookStatus(mainBookService.castBookStatusNameToBookStatus(
//                book.getBookStatus()));
//        book.setLanguage(mainBookService.castBookLanguageNameToBookLanguage(
//                book.getLanguage().getLanguageName()));
//        book.setAuthors(mainBookService.castBookAutorIdentifiersToBookAutors(
//                book.getAuthorIdentifiers()));
//        book.setPublishingHouses(mainBookService.castBookPublishingHouseWOIdToPublishingHouses(
//                book.getPublishingHouses()));
//        book.setGenres(mainBookService.castBookGenresWOIdToBookGenres(
//                book.getGenres()));
        Book updatedBook = mainBookService.updateBook(book);
        return ResponseEntity.status(200).body(updatedBook);
    }
    @Operation(summary = "Добавить статус книги в БД")
    @PostMapping("/addBookStatus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно сохранен новый статус книги"),
            @ApiResponse(responseCode = "444", description = "Такой статус книги уже существует (bookStatus.name)")
    })
    public ResponseEntity<?> addBookStatus(@RequestBody @Validated BookStatus bookStatus){
        var resp = mainBookService.addBookStatus(bookStatus);
        return resp!=null
                ? ResponseEntity.ok(bookStatus)
                : ResponseEntity.status(444).body("Такой статус книги уже существует (bookStatus.name)");
    }
    @Operation(summary = "Добавить жанр книги в БД")
    @PostMapping("/addBookGenre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно сохранен новый жанр книги"),
            @ApiResponse(responseCode = "444", description = "Такой жанр книги уже существует (bookGenre.name)")
    })
    public ResponseEntity<?> addBookStatus(@RequestBody @Validated BookGenre bookGenre){
        var resp = mainBookService.addBookGenre(bookGenre);
        return resp!=null
                ? ResponseEntity.ok(bookGenre)
                : ResponseEntity.status(444).body("Такой жанр книги уже существует (bookStatus.name)");
    }
    @Operation(summary = "Добавить издателя в БД")
    @PostMapping("/addPublishingHouse")
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
    @PostMapping("/addBookAuthor")
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
    @PostMapping("/addBookLanguage")
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
                : ResponseEntity.status(444).body("Такой жанр книги уже существует (bookStatus.name)");
    }
    @DeleteMapping("/deleteBook")
    @Operation(summary = "Удаляет книгу по идентификатору книги")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Книга успешно удалена"),
            @ApiResponse(responseCode = "444", description = "Такой книги не существует")
    })
    public ResponseEntity<?> deleteBookByIdentifier(@RequestParam String identifier){
        var resp = mainBookService.deleteBookByIdentifier(identifier);
        return resp ? ResponseEntity.status(200).build() : ResponseEntity.status(444).build();
    }
}
