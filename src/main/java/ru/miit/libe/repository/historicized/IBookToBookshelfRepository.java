package ru.miit.libe.repository.historicized;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.historicized.BookToBookshelf;

@Repository
public interface IBookToBookshelfRepository extends JpaRepository<BookToBookshelf, Long> {
}
