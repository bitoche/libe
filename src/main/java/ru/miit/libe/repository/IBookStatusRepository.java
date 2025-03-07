package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.BookStatus;

import java.util.Optional;

@Repository
public interface IBookStatusRepository extends JpaRepository<BookStatus, Integer> {
    boolean existsByStatusName(String statusName);
}
