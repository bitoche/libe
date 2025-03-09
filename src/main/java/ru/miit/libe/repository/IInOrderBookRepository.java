package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.miit.libe.models.InOrderBook;

public interface IInOrderBookRepository extends JpaRepository<InOrderBook, Long> {
    // сущность для нормализации OrderingBooks. связь с OrderingBooks (one)-> InOrderBook(many)
}
