package ru.miit.libe.services;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.miit.libe.models.*;
import ru.miit.libe.models.historicized.*;
import ru.miit.libe.repository.historicized.*;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReportsService {
    private IBookToBookStatusRepository bookToBookStatusRepository;
    private IBookToBookshelfRepository bookToBookshelfRepository;
    private IBorrowToBorrowStatusRepository borrowToBorrowStatusRepository;
    private IUserToUserRoleRepository userToUserRoleRepository;
    private IReportEntryRepository reportEntryRepository;

    @Async
    public void addBookToBookStatusAssign(Book b, EBookStatus s){
        var r = new BookToBookStatus();
        r.setStatus(s);
        r.setBook(b);
        r.setAssignDttm(LocalDateTime.now());
        bookToBookStatusRepository.save(r);
    }
    @Async
    public void addBookToBookshelfAssign(Book b, Bookshelf bs, String action){
        var r = new BookToBookshelf();
        r.setBook(b);
        r.setBookshelf(bs);
        r.setAssignDttm(LocalDateTime.now());
        r.setAction(action);
        bookToBookshelfRepository.save(r);
    }
    @Async
    public void addBorrowToBorrowStatusAssign(Borrow b, EBorrowStatus bs){
        var r = new BorrowToBorrowStatus();
        r.setBorrow(b);
        r.setStatus(bs);
        r.setAssignDttm(LocalDateTime.now());
        borrowToBorrowStatusRepository.save(r);
    }
    @Async
    public void addUserToUserRoleAssign(User u, EUserRole rl){
        var r = new UserToUserRole();
        r.setUser(u);
        r.setRole(rl);
        r.setAssignDttm(LocalDateTime.now());
        userToUserRoleRepository.save(r);
    }

    public List<BookToBookStatus> getBookToBookStatusesByPeriod(Date startDate, Date endDate){
        return bookToBookStatusRepository.getAllByAssignDttmBetween(LocalDateTime.parse(startDate.toString()+"T00:00:00"), LocalDateTime.parse(endDate.toString()+"T00:00:00"));
    }
    public List<BookToBookshelf> getBookToBookshelvesByPeriod(Date startDate, Date endDate){
        return bookToBookshelfRepository.getAllByAssignDttmBetween(LocalDateTime.parse(startDate.toString()+"T00:00:00"), LocalDateTime.parse(endDate.toString()+"T00:00:00"));
    }
    public List<UserToUserRole> getUsersToUserRolesByPeriod(Date startDate, Date endDate){
        return userToUserRoleRepository.getAllByAssignDttmBetween(LocalDateTime.parse(startDate.toString()+"T00:00:00"), LocalDateTime.parse(endDate.toString()+"T00:00:00"));
    }
    public List<BorrowToBorrowStatus> getBorrowToBorrowStatusesByPeriod(Date startDate, Date endDate){
        return borrowToBorrowStatusRepository.getAllByAssignDttmBetween(LocalDateTime.parse(startDate.toString()+"T00:00:00"), LocalDateTime.parse(endDate.toString()+"T00:00:00"));
    }

    public ResponseEntity<?> startReports(Date startDate, Date endDate, EReportType reportType){
        final String PYTHON_SERVICE_URL = "http://localhost:5000/api/startReports";
        final RestTemplate restTemplate = new RestTemplate();
        // Показатель "Читаемость" (Readability) — среднее число книг, выданных одному читателю за период.
        // "Обращаемость" (Appeal rate) — среднее число книговыдач на единицу фонда (на книгу).
        // "Книгообеспеченность" (Book security) — среднее количество книг, приходящихся на одного зарегистрированного читателя.
        // "Процент учащихся" (Percentage of students) - общее число пользователей деленное на число студентов
        // "Показатель нагрузки библиотекаря" (Librarian's workload indicator) — Число читателей / на число библиотекарей
        ReportEntry report = new ReportEntry();
        report.setReportType(reportType);
        report.setReportDttm(LocalDateTime.now());
        reportEntryRepository.save(report);

        int calcId = report.getId();

        Map<String, String> params = new HashMap<>();
        params.put("calc_id", String.valueOf(calcId));
        params.put("start_date", startDate.toString());
        params.put("end_date", endDate.toString());
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    PYTHON_SERVICE_URL,
                    params,
                    Map.class
            );

            return ResponseEntity.ok(response.getBody());
        } catch (RestClientException e) {
            return ResponseEntity.status(500).body(
                    Map.of("error", "Python service unavailable")
            );
        }

    }


}