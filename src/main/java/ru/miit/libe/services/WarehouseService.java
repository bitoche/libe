package ru.miit.libe.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.miit.libe.models.*;
import ru.miit.libe.repository.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WarehouseService {
    @Autowired
    IBookRepository bookRepository;
    @Autowired
    ICabinetRepository cabinetRepository;
    @Autowired
    IBookShelfRepository bookShelfRepository;
    @Autowired
    IOrderingBooksRepository orderingBooksRepository;
    @Autowired
    IInOrderBookRepository inOrderBookRepository;

    private final MainBookService mainBookService;

    private final ReportsService reportsService;

    public List<Cabinet> getAllCabinets(){
        return cabinetRepository.findAll().stream().filter(s-> Boolean.TRUE.equals(s.getIsActive())).toList(); // только активные;
    }
    public List<Cabinet> getAllCabinetsByCabinetName(String cabinetName){
        return cabinetRepository.findByCabinetNameContainsIgnoreCase(cabinetName).stream().filter(s-> Boolean.TRUE.equals(s.getIsActive())).toList(); // только активные;
    }

    public List<Bookshelf> getAllBookshelvesByCabinet(int cabinetId){
        if(cabinetId!=-1){
            return bookShelfRepository.findAllByCabinet_CabinetId(cabinetId).stream().filter(s-> Boolean.TRUE.equals(s.getIsActive())).toList(); // только активные
        }
        else return bookShelfRepository.findAllByCabinetNull().stream().filter(s-> Boolean.TRUE.equals(s.getIsActive())).toList(); // только активные
    }

    @Transactional
    public Bookshelf setBookToBookshelf(long bookId, int bookShelfId){
        if (bookRepository.existsById(bookId)){
            var book = bookRepository.findById(bookId).get();
            try{
                var shelf = bookShelfRepository.findById(bookShelfId).get();
                var shelfBooks = shelf.getBooks();
                if(!shelfBooks.contains(book)){
                    shelfBooks.add(book);
                    shelf.setBooks(shelfBooks);
                    reportsService.addBookToBookshelfAssign(book, shelf, "+");
                    bookShelfRepository.save(shelf);
                }
                System.out.println("На полке "+bookShelfId+" уже есть книга "+bookId);
                return shelf;
            }
            catch(Exception e){
                System.out.println("Нет полки с id="+bookShelfId+"\ne:"+e.getMessage());
            }
        }
        return null;
    }
    public void addBookShelf(@Validated Bookshelf bookshelf){
        bookShelfRepository.save(bookshelf);
    }

    // назначить конкретную полку к конкретному шкафу
    @Transactional
    public void assignExistShelfToExistCabinet(int shelfId, int cabinetId, boolean forceFlag){
        var shelf = bookShelfRepository.findById(shelfId);
        var cabinet = cabinetRepository.findById(cabinetId);
        if (shelf.isPresent() && cabinet.isPresent()){
            var s = shelf.get();
            var c = cabinet.get();
            var cab_shelves = c.getShelves();
            if(s.getCabinet() != null){
                System.out.println("shelf "+s.getShelfId()+" already in cabinet "+s.getCabinet().getCabinetId());
                if(forceFlag){
                    s.setCabinet(null);
                }
            }
            if(s.getCabinet()==null){
                if (cab_shelves== null){
                    cab_shelves=new ArrayList<>();
                }
                cab_shelves.add(s);
                c.setShelves(cab_shelves);
                cabinetRepository.save(c);
                s.setCabinet(c);
                bookShelfRepository.save(s);
            }
        }
    }

    // создание шкафа с заданным количеством полок, именем, и rl-айди. при создании может что-то сломаться, поэтому для отката - Transactional
    @Transactional
    public Cabinet createNewCabinet(int shelvesCount, String cabinetName, int cabinetNumber){
        var cab = new Cabinet();
        cab.setCabinetName(cabinetName);
        cab.setCabinetNumber(cabinetNumber);
        cabinetRepository.save(cab);
        List<Bookshelf> shelves = new ArrayList<>();
        for (int i=1; i<=shelvesCount; i++){
            var bookShelf = new Bookshelf();
            bookShelf.setShelfName("Полка №"+String.valueOf(i));
            bookShelf.setBooks(new ArrayList<>());
            bookShelf.setCabinet(cab);
            bookShelfRepository.save(bookShelf);
            shelves.add(bookShelf);
        }
        cab.setShelves(shelves);
        cabinetRepository.save(cab);
        return cab;
    }

    public List<Book> findBooksByBookshelfId(int bookshelfId){
        return bookShelfRepository.findById(bookshelfId).get().getBooks();
    }

    @Transactional
    public List<Cabinet> findCabinetsByBookName(String bookName){
        List<Bookshelf> inBookshelves = bookShelfRepository.findByBooks_BookNameContainsIgnoreCase(bookName);
        List<Cabinet> out = new ArrayList<>();
        for (Bookshelf bs : inBookshelves){
            out.add(bs.getCabinet());
        }
        return out.stream().sorted().toList();
    }

    @Transactional
    public void removeBookFromBookShelf(long bookId, int bookShelfId){
        if (bookRepository.existsById(bookId)){
            var book = bookRepository.findById(bookId).get();
            try{
                var shelf = bookShelfRepository.findById(bookShelfId).get();
                if(shelf.getBooks().contains(book)){
                    var shelfBooks = shelf.getBooks();
                    shelfBooks.remove(book);
                    shelf.setBooks(shelfBooks);
                    reportsService.addBookToBookshelfAssign(book, shelf, "-");
                    bookShelfRepository.save(shelf);
                }
                else System.out.println("На полке "+shelf.getShelfId()+" нет книги "+book.getBookName());
            }
            catch(Exception e){
                System.out.println("Нет полки с id="+bookShelfId+"\ne:"+e.getMessage());
            }
        }
    }

    public Bookshelf removeBookFromShelf(long bookId, int shelfId){
        var b = bookRepository.findById(bookId);
        var s = bookShelfRepository.findById(shelfId);
        if (b.isPresent() && s.isPresent()){
            if(s.get().getBooks().contains(b.get())){
                Bookshelf sh = s.get();
                List<Book> bsh = sh.getBooks();
                bsh.remove(b.get());
                sh.setBooks(bsh);
                bookShelfRepository.save(sh);
                return sh;
            }
            else System.out.println("shelf "+s.get().getShelfId()+" not contains book "+b.get().getBookId());
        }
        else System.out.println("Not defined bookId/shelfId");
        return null;
    }

    @Transactional
    public List<Book> getAllBooksByShelfId(int shelfId){
        return Objects.requireNonNull(bookShelfRepository.findById(shelfId)
                .map(Bookshelf::getBooks).orElse(null)).stream().filter(b-> Boolean.TRUE.equals(b.getIsActive())).toList();
    }

    @Transactional
    public Bookshelf removeShelfFromCabinet(int shelfId){
        var s = bookShelfRepository.findById(shelfId);
        if (s.isPresent()){
            var sh = s.get();
            var cab = sh.getCabinet();
            if(cab!=null){
                var cshelves = cab.getShelves();
                cshelves.remove(sh);
                cab.setShelves(cshelves);
                sh.setCabinet(null);
                cabinetRepository.save(cab);
                bookShelfRepository.save(sh);
                return sh;
            }
            else{
                System.out.println("Shelf is not in cabinet");
                return null;
            }
        }
        else System.out.println("Not defined shelfId");
        return null;
    }

    @Transactional
    public Bookshelf deleteShelf(int shelfId){
        var s = bookShelfRepository.findById(shelfId);
        if(s.isPresent()){
            var sh = s.get();
            var bs = sh.getBooks();
            var cab = sh.getCabinet();
            if(cab != null){
                var cshelves = cab.getShelves();
                cshelves.remove(sh);
                cab.setShelves(cshelves);
                cabinetRepository.save(cab);
            }
            sh.setBooks(null);
            sh.setIsActive(false); // деактивируем
            bookShelfRepository.save(sh);
//            bookShelfRepository.delete(sh);
            return sh;
        }
        else System.out.println("Not defined shelfId");
        return null;
    }

    public Bookshelf renameShelf(int shelfId, String newName){
        var s = bookShelfRepository.findById(shelfId);
        if (s.isPresent()){
           var sh = s.get();
           sh.setShelfName(newName);
           bookShelfRepository.save(sh);
           return sh;
        }
        else System.out.println("Not defined shelfId");
        return null;
    }

    @Transactional
    public Cabinet deleteCabinet(int cabinetId, boolean forceFlag) {
        var c = cabinetRepository.findById(cabinetId);
        if (c.isPresent()){
            var cab = c.get();
            if(forceFlag){
                var shelves = cab.getShelves();
                if(shelves != null && !shelves.isEmpty()){
                    shelves.forEach(s -> bookShelfRepository.delete(s));
                }
            }
            cab.setShelves(null);
            cab.setIsActive(false);
            cabinetRepository.save(cab);
            return cab;
        }
        return null;
    }

    @Transactional
    public OrderingBooks orderBooks(OrderingBooks orderingBooks){
        var inOrderBooks = orderingBooks.getOrderedBooks();
        inOrderBooks.forEach(b->inOrderBookRepository.save(b));
        return orderingBooksRepository.save(orderingBooks);
    }

    public List<OrderingBooks> getAllOrdersByStatus(boolean orderStatus){
        return orderingBooksRepository.findAllByIsActive(orderStatus);
    }
    public List<InOrderBook> getAllInOrderBooksByOrder(long orderId){
        return orderingBooksRepository.findById(orderId)
                .map(OrderingBooks::getOrderedBooks).orElse(null);
    }
}
