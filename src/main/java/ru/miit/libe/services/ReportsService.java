package ru.miit.libe.services;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.miit.libe.models.*;
import ru.miit.libe.models.historicized.*;
import ru.miit.libe.repository.historicized.*;


import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportsService {
    private IBookToBookStatusRepository bookToBookStatusRepository;
    private IBookToBookshelfRepository bookToBookshelfRepository;
    private IBorrowToBorrowStatusRepository borrowToBorrowStatusRepository;
    private IUserToUserRoleRepository userToUserRoleRepository;

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
        return bookToBookStatusRepository.getAllByAssignDttmBetween(LocalDateTime.parse(startDate.toString()), LocalDateTime.parse(endDate.toString()));
    }

}
