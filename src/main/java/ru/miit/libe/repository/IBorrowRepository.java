package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.Borrow;
import ru.miit.libe.models.EBorrowStatus;

import java.util.List;

@Repository
public interface IBorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findAllByBorrowedUser_UserId(long userId);

    List<Borrow> findAllByBorrowedBook_BookId(long bookId);

    List<Borrow> findAllByBorrowStatus(EBorrowStatus borrowStatus);
}
