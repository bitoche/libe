package ru.miit.libe.repository.historicized;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.EBookStatus;
import ru.miit.libe.models.historicized.BookToBookStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface IBookToBookStatusRepository extends JpaRepository<BookToBookStatus, Long> {
    List<BookToBookStatus> getAllByAssignDttmBetween(LocalDateTime assignDttmStart, LocalDateTime assignDttmEnd);
}