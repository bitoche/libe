package ru.miit.libe.services;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.miit.libe.models.Borrow;
import ru.miit.libe.models.EBookStatus;
import ru.miit.libe.models.EBorrowStatus;
import ru.miit.libe.repository.IBookRepository;
import ru.miit.libe.repository.IBorrowRepository;
import ru.miit.libe.repository.IBorrowStatusRepository;
import ru.miit.libe.repository.IUserRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class BorrowService {
    @Autowired
    IBookRepository bookRepository;
    @Autowired
    IBorrowRepository borrowRepository;
    @Autowired
    IUserRepository userRepository;
//    @Autowired
//    IBorrowStatusRepository borrowStatusRepository;
    MainBookService bookService;
    // бронирование книги
    @Transactional
    public Borrow borrow(long userId, long bookId,
                         Date expectedRecieptDate){
        var u = userRepository.findById(userId);
        var b = bookRepository.findById(bookId);
        if(u.isPresent() && b.isPresent()){
            var user = u.get();
            var book = b.get();
            // бронь книги
            book.setBookStatus(EBookStatus.BOOKED);
            Borrow bor = new Borrow();
            bor.setBorrowDttm(LocalDateTime.ofInstant(expectedRecieptDate.toInstant(), ZoneId.systemDefault()));
            bor.setBorrowedUser(user);
            bor.setBorrowedBook(book);
            // ожидает получения
            bor.setBorrowStatus(EBorrowStatus.AWAITING_RECIEPT);
            bookService.updateBook(book);
            borrowRepository.save(bor);
            return bor;
        }
        return null;
    }
    // выдача книги
    @Transactional
    public Borrow extradition(long borrowId, @Nullable LocalDateTime recieptDttm,
                              int redemptionPriceIfLoss, Date expectedReturnDate){
        // стандартно recieptDttm должен быть = curr_timestamp
        if (recieptDttm==null){
            recieptDttm = LocalDateTime.now();
        }
        var bor = borrowRepository.findById(borrowId);
        if(bor.isPresent()){
            var borrow = bor.get();
            // книга теперь выдана
            var book = borrow.getBorrowedBook();
            book.setBookStatus(EBookStatus.ISSUED);
            bookService.updateBook(book);
            // дополняем выдачу
            borrow.setBorrowedBook(book);
            // ожидает получения
            borrow.setBorrowStatus(EBorrowStatus.ON_HANDS);
            borrow.setBorrowDttm(recieptDttm);
            borrow.setRedemptionPriceIfLoss(redemptionPriceIfLoss);
            borrow.setExpectedReturnDate(expectedReturnDate);
            borrowRepository.save(borrow);
            return borrow;
        }
        return null;
    }
    // снять бронь с книги

    // возвращение книги

}
