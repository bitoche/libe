package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.PublishingHouse;
import ru.miit.libe.models.RequestBooks;

import java.util.List;

@Repository
public interface IRequestBooksRepository  extends JpaRepository<RequestBooks, Long> {
    List<RequestBooks> findAllByRequestedUser_UserId(long userId);
}
