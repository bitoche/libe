package ru.miit.libe.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.miit.libe.models.*;
import ru.miit.libe.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    IRequestBooksRepository requestBooksRepository;
    @Autowired
    IInOrderBookRepository inOrderBookRepository;
    NotificationService notificationService;
    MainBookService bookService;
    UserService userService;

    // Форматтер для даты
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ReportsService reportsService;

    // Бронирование книги
    @Transactional
    public Borrow borrow(long userId, long bookId, Date expectedRecieptDate) {
        var u = userRepository.findById(userId);
        var b = bookRepository.findById(bookId);
        if (u.isPresent() && b.isPresent()) {
            var user = u.get();
            var book = b.get();
            if (book.getBookStatus() != EBookStatus.IN_STOCK) {
                return null; // Если книга не в наличии - нельзя
            }
            // Бронь книги
            book.setBookStatus(EBookStatus.BOOKED);
            Borrow bor = new Borrow();
            bor.setBorrowDttm(LocalDateTime.now());
            bor.setExpectedRecieptDate(expectedRecieptDate);
            bor.setBorrowedUser(user);
            bor.setBorrowedBook(book);
            // Ожидает получения
            bor.setBorrowStatus(EBorrowStatus.AWAITING_RECIEPT);
            bookService.updateBook(book);
            borrowRepository.save(bor);
            reportsService.addBorrowToBorrowStatusAssign(bor, bor.getBorrowStatus());

            // Форматируем дату
            String formattedDate = formatDate(expectedRecieptDate);

            // Формируем сообщение с переносами строк
            String message = "Вы успешно зарезервировали книгу \"" + book.getBookName() + "\" на сайте E-Library.\n" +
                    "На данный момент книга ожидает получения до " + formattedDate + " в библиотеке.";

            notificationService.createNewNotification(
                    "Резервирование книги E-Library",
                    message,
                    user.getUserId()
            );
            return bor;
        }
        return null;
    }

    // Выдача книги
    @Transactional
    public Borrow extradition(long borrowId, int redemptionPriceIfLoss, Date expectedReturnDate) {
        var bor = borrowRepository.findById(borrowId);
        if (bor.isPresent()) {
            var borrow = bor.get();
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if (!b.isPresent()) {
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
            reportsService.addBorrowToBorrowStatusAssign(borrow, borrow.getBorrowStatus());
            // Форматируем дату
            String formattedDate = formatDate(expectedReturnDate);

            // Формируем сообщение с переносами строк
            String message = "Вы успешно получили книгу \"" + book.getBookName() + "\".\n" +
                    "Не забудьте вернуть книгу до " + formattedDate + "! Плата при утере книги составит " +
                    borrow.getRedemptionPriceIfLoss() + " рублей.\n" +
                    "Спасибо, " + borrow.getBorrowedUser().getFirstName() + "! Мы вас ценим, обращайтесь к нам ещё!";

            notificationService.createNewNotification(
                    "Получение книги E-Library",
                    message,
                    borrow.getBorrowedUser().getUserId()
            );
            return borrow;
        }
        return null;
    }

    // Возвращение книги
    @Transactional
    public Borrow returnBook(long borrowId) {
        var bor = borrowRepository.findById(borrowId);
        if (bor.isPresent()) {
            var borrow = bor.get();
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if (!b.isPresent()) {
                return null;
            }
            borrow.setFactReturnDttm(LocalDateTime.now());
            borrow.setBorrowStatus(EBorrowStatus.RETURNED);
            borrowRepository.save(borrow);
            var book = b.get();
            book.setBookStatus(EBookStatus.IN_STOCK);
            bookService.updateBook(book);
            reportsService.addBorrowToBorrowStatusAssign(borrow, borrow.getBorrowStatus());
            // Формируем сообщение с переносами строк
            String message = "Вы успешно сдали книгу \"" + book.getBookName() + "\".\n" +
                    "Спасибо, " + borrow.getBorrowedUser().getFirstName() + "! Мы вас ценим, обращайтесь к нам ещё!";

            notificationService.createNewNotification(
                    "Сдача книги E-Library",
                    message,
                    borrow.getBorrowedUser().getUserId()
            );
            return borrow;
        }
        return null;
    }

    // Книга утеряна
    public Borrow lostBook(long borrowId) {
        var bor = borrowRepository.findById(borrowId);
        if (bor.isPresent()) {
            var borrow = bor.get();
            if (borrow.getBorrowStatus() != EBorrowStatus.ON_HANDS) {
                return null; // Если книга не выдана
            }
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if (!b.isPresent()) {
                return null; // Если книги не существует в системе
            }
            borrow.setFactReturnDttm(LocalDateTime.now());
            borrow.setBorrowStatus(EBorrowStatus.LOST);
            borrowRepository.save(borrow);
            var book = b.get();
            book.setBookStatus(EBookStatus.NOT_AVAILABLE);
            bookService.updateBook(book);
            reportsService.addBorrowToBorrowStatusAssign(borrow, borrow.getBorrowStatus());
            // Формируем сообщение с переносами строк
            String message = "Книга \"" + book.getBookName() + "\" отмечена, как утерянная.\n" +
                    borrow.getBorrowedUser().getFirstName() + ", не забудьте оплатить утерю книги, в размере " +
                    borrow.getRedemptionPriceIfLoss() + " рублей!";

            notificationService.createNewNotification(
                    "Утеря книги E-Library",
                    message,
                    borrow.getBorrowedUser().getUserId()
            );
            return borrow;
        }
        return null;
    }

    // Уплачена утеря книги
    public Borrow paidForLostBook(long borrowId) {
        var bor = borrowRepository.findById(borrowId);
        if (bor.isPresent()) {
            var borrow = bor.get();
            if (borrow.getBorrowStatus() != EBorrowStatus.LOST) {
                return null; // Если книга не утеряна
            }
            var b = bookRepository.findById(borrow.getBorrowedBook().getBookId());
            if (!b.isPresent()) {
                return null;
            }
            borrow.setPaidDttm(LocalDateTime.now());
            borrow.setBorrowStatus(EBorrowStatus.LOST_AND_PAID);
            borrowRepository.save(borrow);
            reportsService.addBorrowToBorrowStatusAssign(borrow, borrow.getBorrowStatus());
            // Формируем сообщение с переносами строк
            String message = "Оплата за утерю книги \"" + borrow.getBorrowedBook().getBookName() + "\" в размере " +
                    borrow.getRedemptionPriceIfLoss() + " рублей успешно произведена.\n" +
                    "Спасибо, " + borrow.getBorrowedUser().getFirstName() + "! Мы вас ценим, обращайтесь к нам ещё!";

            notificationService.createNewNotification(
                    "Плата за утерянную книгу E-Library",
                    message,
                    borrow.getBorrowedUser().getUserId()
            );
            return borrow;
        }
        return null;
    }

    // Найти бронь по пользователю
    public List<Borrow> findBorrowByUser(long userId) {
        var u = userRepository.findById(userId);
        if (u.isPresent()) {
            return borrowRepository.findAllByBorrowedUser_UserId(userId);
        }
        return null;
    }

    // Найти бронь по книге
    public List<Borrow> findBorrowByBook(long bookId) {
        var u = bookRepository.findById(bookId);
        if (u.isPresent()) {
            return borrowRepository.findAllByBorrowedBook_BookId(bookId);
        }
        return null;
    }

    // Найти брони по статусу
    public List<Borrow> findBorrowByStatus(EBorrowStatus status) {
        return borrowRepository.findAllByBorrowStatus(status);
    }

    // Запрос книг / изменение запроса / дроп
    public List<RequestBooks> getAllBookRequests() {
        return requestBooksRepository.findAll();
    }

    public List<RequestBooks> getRequestBooksByStatus(boolean isActive) {
        return getAllBookRequests().stream().filter(r -> r.isActive() == isActive).toList();
    }

    public RequestBooks getRequestBooksById(long id) {
        return requestBooksRepository.findById(id).orElse(null);
    }

    @Transactional
    public RequestBooks saveNewRequestBooks(RequestBooks requestBooks) {
        var inOrderBooks = requestBooks.getOrderedBooks();
        inOrderBooks.forEach(b -> inOrderBookRepository.save(b));
        // рассылка всем библиотекарям о получении нового запроса на книги. todo отключено
//        String message = "Получен новый запрос на заказ книг от " + requestBooks.getRequestedUser().getEmail() + ".\n" +
//                "Содержание: " + inOrderBooks;
//        for(User u : userService.getAll()){
//            if (u.getRole() == EUserRole.LIBRARIAN){
//                notificationService.createNewNotification(
//                        "Новый запрос на заказ",
//                        message,
//                        u.getUserId()
//                );
//            }
//        }
        return requestBooksRepository.save(requestBooks);
    }

    @Transactional
    public RequestBooks updateRequestBooks(RequestBooks requestBooks) {
        var newInOrderBooks = requestBooks.getOrderedBooks();
        var oldInOrderBooks = requestBooksRepository.findById(requestBooks.getRequestId()).get().getOrderedBooks();
        oldInOrderBooks.forEach(b -> inOrderBookRepository.delete(b));
        newInOrderBooks.forEach(b -> inOrderBookRepository.save(b));
        //todo notification

        return requestBooksRepository.save(requestBooks);
    }

    public RequestBooks updateStatusOfRequestBooks(RequestBooks requestBooks) {
        requestBooksRepository.save(requestBooks);
        return requestBooks;
    }

    public List<RequestBooks> getRequestBooksByUserId(long userId) {
        return requestBooksRepository.findAllByRequestedUser_UserId(userId);
    }

    public RequestBooks dropRequest(long requestId) {
        var r = getRequestBooksById(requestId);
        if (r != null) {
            requestBooksRepository.deleteById(requestId);
            return r;
        }
        return null;
    }

    // Вспомогательный метод для форматирования даты
    private String formatDate(Date date) {
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .format(DATE_FORMATTER);
    }
}