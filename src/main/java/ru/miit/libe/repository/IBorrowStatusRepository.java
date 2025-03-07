package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.BorrowStatus;

@Repository
public interface IBorrowStatusRepository extends JpaRepository<BorrowStatus, Integer> {
}
