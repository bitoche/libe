package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.dtos.OrderBooksRequest;
import ru.miit.libe.models.EUserRole;
import ru.miit.libe.models.InOrderBook;
import ru.miit.libe.models.OrderingBooks;
import ru.miit.libe.services.MainBookService;
import ru.miit.libe.services.UserService;
import ru.miit.libe.services.WarehouseService;

import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;

@Controller
@RestController
@RequestMapping("/warehouse")
@Tag(name = "Управление выдачей книг, заказами, и т.д. // perm::librarian")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
public class WarehouseController {
    private final WarehouseService warehouseService;
    private final ResponseService rs;
    public WarehouseController(WarehouseService warehouseService, ResponseService rs, UserService userService) {
        this.warehouseService = warehouseService;
        this.rs = rs;
        this.userService = userService;
    }
    @Autowired
    private final UserService userService;
    @Operation(summary = "Получить все книжные шкафы")
    @GetMapping("/cabinets/")
    public ResponseEntity<?> getAllCabinets(){
        return rs.build(warehouseService.getAllCabinets());
    }

    @Operation(summary = "Получить содержимое шкафа")
    @GetMapping("/cabinets/{cabinetId}")
    public ResponseEntity<?> getAllShelvesByCabinet(@PathVariable int cabinetId){
        return rs.build(warehouseService.getAllBookshelvesByCabinet(cabinetId));
    }

    @Operation(summary = "Получить все книги по id полки")
    @GetMapping("/cabinets/{cabinetId}/shelf/{shelfId}")
    public ResponseEntity<?> getAllBooksByShelfId(@PathVariable int shelfId){
        return rs.build(warehouseService.getAllBooksByShelfId(shelfId));
    }

    @Operation(summary = "Добавить новый шкаф")
    @PostMapping("/cabinets/create")
    public ResponseEntity<?> createNewCabinet(@RequestParam int shelvesCount,
                                              @RequestParam String cabinetName,
                                              @RequestParam int cabinetNumber){
        return rs.build(warehouseService.createNewCabinet(shelvesCount, cabinetName, cabinetNumber));
    }

    @Operation(summary = "Удалить шкаф")
    @DeleteMapping("/cabinets/delete")
    public ResponseEntity<?> deleteCabinet(@RequestParam int cabinetId,
                                           @RequestParam boolean deleteInnerShelves){
        return rs.build(warehouseService.deleteCabinet(cabinetId, deleteInnerShelves));
    }

    @Operation(summary = "Убрать полку из шкафа (не удаляет полку целиком)")
    @PatchMapping("/shelves/{shelfId}/remove")
    public ResponseEntity<?> removeShelfFromCabinet(@PathVariable int shelfId){
        return rs.build(warehouseService.removeShelfFromCabinet(shelfId));
    }

    @Operation(summary = "Удалить полку целиком")
    @PostMapping("/shelves/{shelfId}/delete")
    public ResponseEntity<?> removeShelfFully(@RequestParam int shelfId){
        return rs.build(warehouseService.deleteShelf(shelfId));
    }
    @Operation(summary = "Получить все полки не в шкафах")
    @GetMapping("/shelves/empty-cabinet")
    public ResponseEntity<?> getShelvesWOCabinet(){
        return rs.build(warehouseService.getAllBookshelvesByCabinet(-1));
    }

    @Operation(summary = "Переименовать полку")
    @PostMapping("/shelves/{shelfId}/rename")
    public ResponseEntity<?> renameShelf(@PathVariable int shelfId, String newName){
        return rs.build(warehouseService.renameShelf(shelfId, newName));
    }

    @Operation(summary = "Переместить существующую полку в шкаф")
    @PostMapping("/shelves/{shelfId}/move-to/{cabinetId}")
    public ResponseEntity<?> replaceShelf(@PathVariable int shelfId,@PathVariable int cabinetId, boolean forceFlag){
        warehouseService.assignExistShelfToExistCabinet(shelfId, cabinetId, forceFlag);
        // todo переделать на норм вывод
        return ResponseEntity.ok().body("Полка "+shelfId+ " успешно перемещена в шкаф "+cabinetId);
    }

    @Operation(summary = "Добавить книгу на полку")
    @PostMapping("/shelves/{shelfId}/add-book/{bookId}")
    public ResponseEntity<?> addBookToShelf(@PathVariable int bookId,@PathVariable  int shelfId){
        return rs.build(warehouseService.setBookToBookshelf(bookId, shelfId));
    }

    @Operation(summary = "Убрать книгу с полки")
    @PostMapping("/shelves/{shelfId}/remove-from-cabinet")
    public ResponseEntity<?> removeBookFromShelf(int bookId, int shelfId){
        return rs.build(warehouseService.removeBookFromShelf(bookId, shelfId));
    }

    // Заказ книг (`ordering_books`) и т.п.
    @Operation(summary = "Заказать книги (должна быть привязана логика заказа у поставщика)")
    @PostMapping("/orders/create")
    public ResponseEntity<?> orderBooks(@RequestBody OrderBooksRequest obr
                                        //@RequestParam Date expectedArrivalDate,
                                        //@RequestParam long orderedLibrarianId,
                                        //@RequestBody List<InOrderBook> orderedBooks,
                                        //@RequestParam @Nullable String comment
            /*@RequestBody OrderingBooks orderingBooks*/){
        // проверка на админа
        if(userService.checkUserNotExists(null, obr.getOrderedLibrarianId())){
            return ResponseEntity.badRequest().body("user "+obr.getOrderedLibrarianId()+" not found");
        }
        var user = userService.getUser(obr.getOrderedLibrarianId(), null);
        if(user.getRole()!= EUserRole.LIBRARIAN){
            return ResponseEntity.badRequest().body("user "+obr.getOrderedLibrarianId()+" is not librarian");
        } // а вдруг?
        OrderingBooks order = new OrderingBooks();
        order.setOrderingDttm(LocalDateTime.now());
        order.setOrderedBooks(obr.getOrderedBooks());
        order.setOrderedAdministrator(user);
        order.setActive(true);
        order.setComment(obr.getComment());
        order.setExpectedArrivalDate(obr.getExpectedArrivalDate());
        return rs.build(warehouseService.orderBooks(order));
    }
    @Operation(summary = "Получить все заказы по статусу")
    @GetMapping("/orders/search/status")
    public ResponseEntity<?> getOrdersByStatus(@RequestParam boolean isActive){
        return rs.build(warehouseService.getAllOrdersByStatus(isActive));
    }
    @Operation(summary = "Получить содержимое заказа")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getInOrderBooksByOrder(@PathVariable long orderId){
        return rs.build(warehouseService.getAllInOrderBooksByOrder(orderId));
    }
    // Приемка книг (`books_arriving`)
}
