package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.OrderingBooks;

import java.util.List;

@Repository
public interface IOrderingBooksRepository extends JpaRepository<OrderingBooks, Long> {
    List<OrderingBooks> findAllByIsActive(boolean isActive);
}
