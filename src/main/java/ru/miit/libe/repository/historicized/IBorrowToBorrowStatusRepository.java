package ru.miit.libe.repository.historicized;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.historicized.BorrowToBorrowStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IBorrowToBorrowStatusRepository extends JpaRepository<BorrowToBorrowStatus, Long> {
    List<BorrowToBorrowStatus> getAllByAssignDttmBetween(LocalDateTime assignDttmStart, LocalDateTime assignDttmEnd);
}