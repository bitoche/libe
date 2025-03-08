package ru.miit.libe.services;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.miit.libe.repository.*;
import ru.miit.libe.models.*;


import java.util.*;

@Service
public class MainBookService {
    @Autowired
    IBookRepository bookRepository;
    @Autowired
    IBookStatusRepository bookStatusRepository;
    @Autowired
    IBookGenreRepository bookGenreRepository;
    @Autowired
    IPublishingHouseRepository publishingHouseRepository;
    @Autowired
    IBookAuthorRepository bookAuthorRepository;
    @Autowired
    IBookLanguageRepository bookLanguageRepository;

    public MainBookService(IBookAuthorRepository bookAuthorRepository,
                           IPublishingHouseRepository publishingHouseRepository,
                           IBookRepository bookRepository,
                           IBookStatusRepository bookStatusRepository,
                           IBookGenreRepository bookGenreRepository,
                           IBookLanguageRepository bookLanguageRepository) {
        this.bookRepository = bookRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.bookGenreRepository = bookGenreRepository;
        this.publishingHouseRepository = publishingHouseRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookLanguageRepository = bookLanguageRepository;
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }
    public List<BookStatus> getAllBookStatuses(){
        return bookStatusRepository.findAll();
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
        List<Book> resp = new ArrayList<>();
        List<String> searchRequestParts = Arrays.stream(searchRequest.split(" ")).toList();
        for (String partOfRequest
                : searchRequestParts){
            var byGenre = bookRepository.findAllByGenres_GenreNameIn(Collections.singleton(partOfRequest));
            var byAuthorIdentifier = bookRepository.findAllByAuthors_IdentifierIn(Collections.singleton(partOfRequest));
            var byName = bookRepository.findAllByBookNameContains(partOfRequest);
            var byIdentifier = bookRepository.findAllByAuthors_IdentifierIn(Collections.singleton(partOfRequest));
            if(!byIdentifier.isEmpty()){
                resp.addAll(byIdentifier);
            }
            if (!byName.isEmpty()){
                resp.addAll(byName);
            }
            if(!byGenre.isEmpty()){
                resp.addAll(byGenre);
            }
            if(!byAuthorIdentifier.isEmpty()){
                resp.addAll(byAuthorIdentifier);
            }
        }
        return resp;
    }

    public Book addBook(@NotNull Book book){
        bookRepository.save(book);
        return book;
    }

    public BookStatus addBookStatus(@NotNull BookStatus bookStatus){
        if (!bookStatusRepository.existsByStatusName(bookStatus.getStatusName())){
            return bookStatusRepository.save(bookStatus);
        }
        return null;
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

    public Book deleteBookByIdentifier(String identifier) {
        if (bookRepository.existsByIdentifier(identifier)) {
            var book = bookRepository.findByIdentifier(identifier);
            bookRepository.delete(book); // Удалить объект
            return book;
        }
        return null;
    }

    public Book updateBook(Book updatedBook){
        if (bookRepository.existsByBookId(updatedBook.getBookId())) {
            bookRepository.save(updatedBook);
            return updatedBook;
        }
        return null;
    }
}
