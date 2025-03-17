package ru.miit.libe.repository.historicized;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.historicized.BorrowToBorrowStatus;

@Repository
public interface IBorrowToBorrowStatusRepository extends JpaRepository<BorrowToBorrowStatus, Long> {
}