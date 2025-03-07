package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.BooksArriving;

@Repository
public interface IBooksArrivingRepository extends JpaRepository<BooksArriving, Long> {
}
