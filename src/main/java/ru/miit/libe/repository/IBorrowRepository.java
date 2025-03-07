package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.Borrow;

@Repository
public interface IBorrowRepository extends JpaRepository<Borrow, Long> {
}
