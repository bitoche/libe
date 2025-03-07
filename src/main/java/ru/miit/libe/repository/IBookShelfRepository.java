package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.Bookshelf;

import java.util.List;

@Repository
public interface IBookShelfRepository extends JpaRepository<Bookshelf, Integer> {
    List<Bookshelf> findAllByCabinet_CabinetId(int cabinetId);

    List<Bookshelf> findAllByCabinetNull();

    List<Bookshelf> findByBooks_BookNameContainsIgnoreCase(String bookName);
}
