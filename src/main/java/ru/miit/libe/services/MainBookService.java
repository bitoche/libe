package ru.miit.libe.services;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.miit.libe.repository.*;
import ru.miit.libe.models.*;
import ru.miit.libe.services.CRUD.CRUDBookService;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MainBookService {
    ReportsService reportsService;
    @Autowired
    IBookRepository bookRepository;
//    @Autowired
//    IBookStatusRepository bookStatusRepository;
    @Autowired
    IBookGenreRepository bookGenreRepository;
    @Autowired
    IPublishingHouseRepository publishingHouseRepository;
    @Autowired
    IBookAuthorRepository bookAuthorRepository;
    @Autowired
    IBookLanguageRepository bookLanguageRepository;
    CRUDBookService cbs;

    public MainBookService(IBookAuthorRepository bookAuthorRepository,
                           IPublishingHouseRepository publishingHouseRepository,
                           IBookRepository bookRepository,
                           //IBookStatusRepository bookStatusRepository,
                           IBookGenreRepository bookGenreRepository,
                           IBookLanguageRepository bookLanguageRepository,
                           CRUDBookService cbs) {
        this.bookRepository = bookRepository;
        //this.bookStatusRepository = bookStatusRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.publishingHouseRepository = publishingHouseRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookLanguageRepository = bookLanguageRepository;
        this.cbs = cbs;
    }

    public List<Book> getAllBooks(){
        return cbs.getAll().stream().filter(b-> Boolean.TRUE.equals(b.getIsActive())).toList(); // возвращаем только активные
    }
    public List<EBookStatus> getAllBookStatuses(){
        return List.of(EBookStatus.values());
    }
    public List<BookGenre> getAllBookGenres(){
        return bookGenreRepository.findAll();
    }
    public List<PublishingHouse> getAllPublishingHouses(){
        return publishingHouseRepository.findAll();
    }
    public List<BookAuthor> getAllBookAuthors(){
        return bookAuthorRepository.findAll();
    }
    public List<BookLanguage> getAllBookLanguages(){
        return bookLanguageRepository.findAll();
    }
    public List<Book> searchBooksFromSearchField(String searchRequest){
        Map<Book, Integer> resp = new HashMap<>(); // Book - key, Integer - matches_count
        List<String> searchRequestParts = Arrays.stream(searchRequest.split(" "))
                .filter(part -> part.length() >= 3)
                .toList();
        var allBooks = cbs.getAll();
        for (String partOfRequest
                : searchRequestParts){
            for (Book book: allBooks){
                if(book.toString().toLowerCase().contains(partOfRequest.toLowerCase())){
                    if(!resp.containsKey(book)){
                        resp.put(book, 1);
                    }
                    else resp.put(book, resp.get(book)+1);
                }
            }
        }
        // sort
        return resp.entrySet().stream()
                .sorted(Map.Entry.<Book, Integer>comparingByValue().reversed()) // Сортировка по убыванию
                .map(Map.Entry::getKey) // Преобразование в список книг
                .distinct() // Удаление дубликатов (если вдруг)
                .collect(Collectors.toList());
    }

    public Book addBook(@NotNull Book book){
        var r = cbs.saveBook(book);
        reportsService.addBookToBookStatusAssign(book, book.getBookStatus());
        return r;
    }
    public BookGenre addBookGenre(@NotNull BookGenre bookGenre){
        if (!bookGenreRepository.existsByGenreName(bookGenre.getGenreName())){
            return bookGenreRepository.save(bookGenre);
        }
        return null;
    }
    public PublishingHouse addPublishingHouse(@NotNull PublishingHouse publishingHouse){
        if (!publishingHouseRepository.existsByPublishingHouseName(publishingHouse.getPublishingHouseName())){
            return publishingHouseRepository.save(publishingHouse);
        }
        return null;
    }
    public BookAuthor addBookAuthor(@NotNull BookAuthor bookAuthor){
        if (!bookAuthorRepository.existsByIdentifier(bookAuthor.getIdentifier())){
            return bookAuthorRepository.save(bookAuthor);
        }
        return null;
    }
    public BookLanguage addBookLanguage(@NotNull BookLanguage bookLanguage){
        if (!bookLanguageRepository.existsByLanguageName(bookLanguage.getLanguageName())){
            return bookLanguageRepository.save(bookLanguage);
        }
        return null;
    }

    public Book deleteBookById(Long id) {
        if(bookRepository.existsByBookId(id)){
            var b = cbs.getById(id);
            b.setIsActive(false);
            return updateBook(b);
        }
        return null;
    }

    public Book updateBook(Book updatedBook){
        var b = bookRepository.getByBookId(updatedBook.getBookId());
        if (b.isEmpty()){
            return null;
        }
        if(b.get().getBookStatus() != updatedBook.getBookStatus()){
            reportsService.addBookToBookStatusAssign(b.get(), b.get().getBookStatus());
        }
        return cbs.saveBook(updatedBook);
    }


}
