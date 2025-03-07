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
//        for (Book book : r) {
//            Hibernate.initialize(book.getAuthors());
//            Hibernate.initialize(book.getGenres());
//            Hibernate.initialize(book.getPublishingHouses());
//        }
        return bookRepository.findAll();
    }
    public List<BookStatus> getAllBookStatuses(){
//        List<BookStatus> allBookStatuses = bookStatusRepository.findAll();
//        if(allBookStatuses.isEmpty()){
//            return null;
//        }
        return bookStatusRepository.findAll();
    }
    public List<BookGenre> getAllBookGenres(){
//        List<BookGenre> allBookGenres = bookGenreRepository.findAll();
//        if(allBookGenres.isEmpty()){
//            return null;
//        }
        return bookGenreRepository.findAll();
    }
    public List<PublishingHouse> getAllPublishingHouses(){
//        List<PublishingHouse> allPublishingHouses = publishingHouseRepository.findAll();
//        if(allPublishingHouses.isEmpty()){
//            return null;
//        }
        return publishingHouseRepository.findAll();
    }
    public List<BookAuthor> getAllBookAuthors(){
//        List<BookAuthor> allBookAuthors = bookAuthorRepository.findAll();
//        if(allBookAuthors.isEmpty()){
//            return null;
//        }
        return bookAuthorRepository.findAll();
    }
    public List<BookLanguage> getAllBookLanguages(){
//        List<BookLanguage> allBookLanguages = bookLanguageRepository.findAll();
//        if(allBookLanguages.isEmpty()){
//            return null;
//        }
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
// todo delete if unused

//    @Autowired
//    private JdbcTemplate jdbcTemplate; // для транзакции
//    @Transactional
//    public Book createBookWithSQL(Book book) {
//        if (bookRepository.existsByIdentifier(book.getIdentifier())){
//            throw new IllegalArgumentException("Book with identifier "+ book.getIdentifier() + " already exists");
//        }
//        // Вставляем книгу и получаем её ID
//        String sql = "INSERT INTO public.book (book_name, book_status, description, identifier, number_of_pages, release_date, language) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING book_id";
//        Long bookId = jdbcTemplate.queryForObject(sql, Long.class, book.getBookName(), book.getBookStatus().getBookStatusId(),
//                book.getDescription(), book.getIdentifier(), book.getPagesNumber(), book.getReleaseDate(), book.getLanguage().getLanguageId());
//
//        // Связываем книгу с жанрами
//        for (BookGenre genre : book.getGenres()) {
//            String genreSql = "INSERT INTO book_to_genre (book_id, genre_id) VALUES (?, ?)";
//            jdbcTemplate.update(genreSql, bookId, genre.getGenreId());
//        }
//
//        // Связываем книгу с авторами
//        for (BookAuthor author : book.getAuthors()) {
//            String authorSql = "INSERT INTO book_to_author (book_id, book_author_id) VALUES (?, ?)";
//            jdbcTemplate.update(authorSql, bookId, author.getAuthorId());
//        }
//
//        // Связываем книгу с издательствами
//        for (PublishingHouse publishingHouse : book.getPublishingHouses()) {
//            String publishingHouseSql = "INSERT INTO book_to_publishing_house (book_id, publishing_house_id) VALUES (?, ?)";
//            jdbcTemplate.update(publishingHouseSql, bookId, publishingHouse.getPublishingHouseId());
//        }
//        return book;
//    } //добавление книг с помощью транзакции sql-кодом

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



//    // каст стринг-полей в объекты по названию
//    public BookLanguage castBookLanguageNameToBookLanguage(String bookLanguageName){
//        if(bookLanguageRepository.existsByLanguageName(bookLanguageName)){
//            return bookLanguageRepository.findBookLanguageByLanguageName(bookLanguageName)
//                    .orElseThrow(() -> new RuntimeException("BookLanguage with name " + bookLanguageName + " not found"));
//        }
//        else {
//            // Можно выбросить исключение или установить дефолтное значение для языка
//            throw new IllegalArgumentException("Язык не найден в БД");
//        }
//    }

//    public BookStatus castBookStatusNameToBookStatus(BookStatus bookStatus) {
//        if(bookStatusRepository.existsByStatusName(bookStatus.getStatusName())){
//            return bookStatusRepository.findBookStatusByStatusName(bookStatus.getStatusName())
//                    .orElseThrow(() -> new RuntimeException("BookStatus with name " + bookStatus.getStatusName() + " not found"));
//        }
//        return null;
//    }

//    public Set<BookAuthor> castBookAutorIdentifiersToBookAutors(Set<String> authorIdentifiers) {
//        Set<BookAuthor> authors = new HashSet<>();
//        for (String identifier:
//        authorIdentifiers){
//            if(bookAuthorRepository.existsByIdentifier(identifier)){
//                authors.add(bookAuthorRepository.findBookAuthorByIdentifier(identifier)
//                        .orElseThrow(() -> new RuntimeException("Author with identifier " + identifier + " not found")));
//            }
//        }
//        return authors;
//    }

//    public Set<PublishingHouse> castBookPublishingHouseWOIdToPublishingHouses(Set<PublishingHouse> publishingHouses){
//        Set<PublishingHouse> pHouses = new HashSet<>(); // pHouses = publishing Houses
//        for (PublishingHouse cph: // current publishing house
//                publishingHouses){
//            if(publishingHouseRepository.existsByPublishingHouseName(cph.getPublishingHouseName())){
//                pHouses.add(publishingHouseRepository.findByPublishingHouseName(cph.getPublishingHouseName())
//                        .orElseThrow(() -> new RuntimeException("PublishingHouse with name " + cph.getPublishingHouseName() + " not found")));
//            }
//        }
//        return pHouses;
//    }

//    public Set<BookGenre> castBookGenresWOIdToBookGenres(Set<BookGenre> genres) {
//        Set<BookGenre> outGenres = new HashSet<>();
//        for (BookGenre cGenre: // current genre
//                genres){
//            if(bookGenreRepository.existsByGenreName(cGenre.getGenreName())){
//                outGenres.add(bookGenreRepository.findByGenreName(cGenre.getGenreName())
//                        .orElseThrow(() -> new RuntimeException("BookGenre with name " + cGenre.getGenreName() + " not found")));
//            }
//        }
//        return outGenres;
//    }


    public boolean deleteBookByIdentifier(String identifier) {
        if (bookRepository.existsByIdentifier(identifier)) {
//            Book book = bookRepository.findByIdentifier(identifier); // Загрузить сущность
//            book.getPublishingHouses().clear(); // Очистить связанные коллекции
//            book.getAuthors().clear();
//            book.getGenres().clear();
            bookRepository.delete(bookRepository.findByIdentifier(identifier)); // Удалить объект
            return true;
        }
        return false;
    }

    public Book updateBook(Book updatedBook){
        if (bookRepository.existsByBookId(updatedBook.getBookId())) {
            bookRepository.save(updatedBook);
            return updatedBook;
        }
        return null;
    }

//    @Transactional
//    public Book updateBookWithSQL(Book updatedBook) {
//        // Проверяем, существует ли книга
//        if (!bookRepository.existsByIdentifier(updatedBook.getIdentifier())) {
//            throw new IllegalArgumentException("Book with identifier " + updatedBook.getIdentifier() + " does not exist");
//        }
//
//        // Получаем ID книги по её идентификатору
//        String findIdSql = "SELECT book_id FROM public.book WHERE identifier = ?";
//        Long bookId = jdbcTemplate.queryForObject(findIdSql, Long.class, updatedBook.getIdentifier());
//
//        // Обновляем основную информацию о книге
//        String updateSql = "UPDATE public.book SET book_name = ?, book_status = ?, description = ?, number_of_pages = ?, release_date = ?, language = ? " +
//                "WHERE book_id = ?";
//        jdbcTemplate.update(updateSql,
//                updatedBook.getBookName(),
//                updatedBook.getBookStatus().getBookStatusId(),
//                updatedBook.getDescription(),
//                updatedBook.getPagesNumber(),
//                updatedBook.getReleaseDate(),
//                updatedBook.getLanguage().getLanguageId(),
//                bookId
//        );
//
//        // Удаляем старые записи в связанных таблицах
//        String deleteGenresSql = "DELETE FROM book_to_genre WHERE book_id = ?";
//        jdbcTemplate.update(deleteGenresSql, bookId);
//
//        String deleteAuthorsSql = "DELETE FROM book_to_author WHERE book_id = ?";
//        jdbcTemplate.update(deleteAuthorsSql, bookId);
//
//        String deletePublishingHousesSql = "DELETE FROM book_to_publishing_house WHERE book_id = ?";
//        jdbcTemplate.update(deletePublishingHousesSql, bookId);
//
//        // Добавляем новые жанры
//        for (BookGenre genre : updatedBook.getGenres()) {
//            String genreSql = "INSERT INTO book_to_genre (book_id, genre_id) VALUES (?, ?)";
//            jdbcTemplate.update(genreSql, bookId, genre.getGenreId());
//        }
//
//        // Добавляем новых авторов
//        for (BookAuthor author : updatedBook.getAuthors()) {
//            String authorSql = "INSERT INTO book_to_author (book_id, book_author_id) VALUES (?, ?)";
//            jdbcTemplate.update(authorSql, bookId, author.getAuthorId());
//        }
//
//        // Добавляем новые издательства
//        for (PublishingHouse publishingHouse : updatedBook.getPublishingHouses()) {
//            String publishingHouseSql = "INSERT INTO book_to_publishing_house (book_id, publishing_house_id) VALUES (?, ?)";
//            jdbcTemplate.update(publishingHouseSql, bookId, publishingHouse.getPublishingHouseId());
//        }
//
//        return updatedBook;
//    }

}
