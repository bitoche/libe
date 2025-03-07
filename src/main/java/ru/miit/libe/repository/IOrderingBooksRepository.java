package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.OrderingBooks;

@Repository
public interface IOrderingBooksRepository extends JpaRepository<OrderingBooks, Long> {
}
