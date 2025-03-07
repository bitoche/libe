package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.BookAuthor;

import java.util.Optional;

@Repository
public interface IBookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    boolean existsByIdentifier(String identifier);
}
