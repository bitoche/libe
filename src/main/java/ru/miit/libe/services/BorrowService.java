package ru.miit.libe.services;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
import java.util.List;

@AllArgsConstructor
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
            if(book.getBookStatus()!=EBookStatus.IN_STOCK){
                return null; // если книга не в наличии - нельзя
            }
            // бронь книги
            book.setBookStatus(EBookStatus.BOOKED);
            Borrow bor = new Borrow();
            bor.setBorrowDttm(LocalDateTime.now());
            bor.setExpectedRecieptDate(expectedRecieptDate);
            bor.setBorrowedUser(user);
            bor.setBorrowedBook(book);
            // ожидает получения
            bor.setBorrowStatus(EBorrowStatus.AWAITING_RECIEPT);
            bookService.updateBook(book);
            borrowRepository.save(bor);
            // на этом этапе у брони заполнены поля:
            // status == AWAITING_RECIEPT
            // borrowDttm == now
            // expected reciept date
            // borrowed user
            // borrowed book (book status = booked)
            return bor;
        }
        return null;
    }
    // выдача книги
    @Transactional
    public Borrow extradition(long borrowId, int redemptionPriceIfLoss, Date expectedReturnDate){
        var bor = borrowRepository.findById(borrowId);
        if(bor.isPresent()){
            var borrow = bor.get();
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if(!b.isPresent()){
                return null;
            }
            borrow.setExpectedReturnDate(expectedReturnDate);
            borrow.setBorrowStatus(EBorrowStatus.ON_HANDS);
            borrow.setRedemptionPriceIfLoss(redemptionPriceIfLoss);
            borrow.setFactRecieptDttm(LocalDateTime.now());
            borrowRepository.save(borrow);
            var book = b.get();
            book.setBookStatus(EBookStatus.ISSUED);
            bookService.updateBook(book);
            return borrow;
            // на данном этапе у брони заполнены поля:
            // status == ON_HANDS
            // borrowDttm //
            // expected reciept date //
            // factRecieptDttm = now
            // borrowed user //
            // borrowed book (book status = ISSUED)
            // redemptionPriceIfLoss
            // expectedReturnDate
        }
        return null;
    }
    // снять бронь с книги

    // возвращение книги
    @Transactional
    public Borrow returnBook(long borrowId){
        var bor = borrowRepository.findById(borrowId);
        if(bor.isPresent()){
            var borrow = bor.get();
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if(!b.isPresent()){
                return null;
            }
            borrow.setFactReturnDttm(LocalDateTime.now());
            borrow.setBorrowStatus(EBorrowStatus.RETURNED);
            borrowRepository.save(borrow);
            var book = b.get();
            book.setBookStatus(EBookStatus.IN_STOCK);
            bookService.updateBook(book);
            return borrow;
        }
        return null;
    }
    // книга утеряна
    public Borrow lostBook(long borrowId){
        var bor = borrowRepository.findById(borrowId);
        if(bor.isPresent()){
            var borrow = bor.get();
            if(borrow.getBorrowStatus()!=EBorrowStatus.ON_HANDS){
                return null; // если книга не выдана
            }
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if(!b.isPresent()){
                return null; // если книги не существует в системе
            }
            borrow.setFactReturnDttm(LocalDateTime.now());
            borrow.setBorrowStatus(EBorrowStatus.LOST);
            borrowRepository.save(borrow);
            var book = b.get();
            book.setBookStatus(EBookStatus.NOT_AVAILABLE);
            bookService.updateBook(book);
            return borrow;
        }
        return null;
    }
    // уплачена утеря книги
    public Borrow paidForLostBook(long borrowId){
        var bor = borrowRepository.findById(borrowId);
        if(bor.isPresent()){
            var borrow = bor.get();
            if(borrow.getBorrowStatus()!=EBorrowStatus.LOST){
                return null; // если книга не утеряна
            }
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if(!b.isPresent()){
                return null;
            }
            borrow.setPaidDttm(LocalDateTime.now());
            borrow.setBorrowStatus(EBorrowStatus.LOST_AND_PAID);
            borrowRepository.save(borrow);
            return borrow;
        }
        return null;
    }
    // найти бронь по пользователю
    public List<Borrow> findBorrowByUser(long userId){
        var u = userRepository.findById(userId);
        if(u.isPresent()){
            return borrowRepository.findAllByBorrowedUser_UserId(userId);
        }
        return null;
    }
    // найти бронь по книге
    public List<Borrow> findBorrowByBook(long bookId){
        var u = bookRepository.findById(bookId);
        if(u.isPresent()){
            return borrowRepository.findAllByBorrowedBook_BookId(bookId);
        }
        return null;
    }
    // найти брони по статусу
    public List<Borrow> findBorrowByStatus(EBorrowStatus status){
        return borrowRepository.findAllByBorrowStatus(status);
    }
}
