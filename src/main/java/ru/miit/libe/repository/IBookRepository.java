package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.Book;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {
    boolean existsByIdentifier(String identifier);

    List<Book> findAllByGenres_GenreNameIn(Collection<String> text);

    List<Book> findAllByAuthors_IdentifierIn(Collection<String> text);

    List<Book> findAllByBookNameContains(String bookName);

    //Book findByIdentifier(String identifier);

    boolean existsByBookId(long bookId);

    //List<Book> findByBookshelf_ShelfId(long shelfId);

    Optional<Book> findByIdentifier(String identifier);

    List<Book> findAllByAuthors_FirstNameContainsIgnoreCaseOrAuthors_SecondNameContainsIgnoreCaseOrAuthors_ThirdNameContainsIgnoreCase(String firstName, String secondName, String thirdName);

    List<Book> getBooksByAuthors_AuthorId(long authorId);

    Optional<Book> getByBookId(long bookId);
}
