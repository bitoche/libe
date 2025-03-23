package ru.miit.libe.repository.historicized;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.historicized.ReportEntry;

@Repository
public interface IReportEntryRepository extends JpaRepository<ReportEntry, Integer> {
}
