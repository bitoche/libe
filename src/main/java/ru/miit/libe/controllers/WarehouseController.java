package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.services.MainBookService;
import ru.miit.libe.services.WarehouseService;

@Controller
@RestController
@RequestMapping("/api/adm/warehouse")
@Tag(name = "Управление выдачей книг, заказами, и т.д. // perm::librarian/admin")
@CrossOrigin("http://localhost:3000/")
public class WarehouseController {
    private final MainBookService mainBookService;
    private final WarehouseService warehouseService;
    public WarehouseController(MainBookService mainBookService, WarehouseService warehouseService) {
        this.mainBookService = mainBookService;
        this.warehouseService = warehouseService;
    }

    @Operation(summary = "Получить все книжные шкафы")
    @GetMapping("/")
    public ResponseEntity<?> getAllCabinets(){
        var allCabinets = warehouseService.getAllCabinets();
        return allCabinets !=null
                ? ResponseEntity.ok(allCabinets)
                : ResponseEntity.status(499).body("Не найдено ни одного шкафа");
    }

    @Operation(summary = "Получить все полки по id шкафа")
    @GetMapping("/{cabinetId}")
    public ResponseEntity<?> getAllShelvesByCabinet(@PathVariable int cabinetId){
        var allShelves = warehouseService.getAllBookshelvesByCabinet(cabinetId);
        return allShelves !=null
                ? ResponseEntity.ok(allShelves)
                : ResponseEntity.status(499).body("Не найдено ни одной полки для этого шкафа");
    }

    @Operation(summary = "Получить все книги по id полки")
    @GetMapping("/s/{shelfId}")
    public ResponseEntity<?> getAllBooksByShelfId(@PathVariable int shelfId){
        var allBooks = warehouseService.getAllBooksByShelfId(shelfId);
        return allBooks !=null
                ? ResponseEntity.ok(allBooks)
                : ResponseEntity.status(499).body("Не найдено ни одной книги для этой полки");
    }

    @Operation(summary = "Добавить новый шкаф")
    @PostMapping("/createCabinet")
    public ResponseEntity<?> createNewCabinet(@RequestParam int shelvesCount,
                                              @RequestParam String cabinetName,
                                              @RequestParam int cabinetNumber){
        var createdCabinet = warehouseService.createNewCabinet(shelvesCount, cabinetName, cabinetNumber);
        return createdCabinet !=null
                ? ResponseEntity.ok(createdCabinet)
                : ResponseEntity.status(499).body("Шкаф не создан.");
    }

    @Operation(summary = "Убрать полку из шкафа (не удаляет полку целиком)")
    @PostMapping("/removeShelfFromCabinet")
    public ResponseEntity<?> removeShelfFromCabinet(@RequestParam int shelfId){
        var removedShelf = warehouseService.removeShelfFromCabinet(shelfId);
        return removedShelf !=null
                ? ResponseEntity.ok(removedShelf)
                : ResponseEntity.status(499).body("Полка не убрана из шкафа.");
    }

    @Operation(summary = "Удалить полку целиком")
    @PostMapping("/removeShelfFully")
    public ResponseEntity<?> removeShelfFully(@RequestParam int shelfId){
        var deletedShelf = warehouseService.deleteShelf(shelfId);
        return deletedShelf !=null
                ? ResponseEntity.ok(deletedShelf)
                : ResponseEntity.status(499).body("Полка не удалена.");
    }
    @Operation(summary = "Получить все полки не в шкафах")
    @GetMapping("/getShelvesWOCabinet")
    public ResponseEntity<?> getShelvesWOCabinet(){
        var resp = warehouseService.getAllBookshelvesByCabinet(-1);
        return !resp.isEmpty() ? ResponseEntity.ok().body(resp) : ResponseEntity.badRequest().body("Нет полок не в шкафах");
    }
    @Operation(summary = "Переименовать полку")
    @PostMapping("/renameShelf")
    public ResponseEntity<?> renameShelf(int shelfId, String newName){
        var resp = warehouseService.renameShelf(shelfId, newName);
        return resp!=null ? ResponseEntity.ok().body(resp) : ResponseEntity.badRequest().body("Полка не переименована.");
    }

    @Operation(summary = "Переместить существующую полку в шкаф")
    @PostMapping("/replaceShelf")
    public ResponseEntity<?> replaceShelf(int shelfId, int cabinetId, boolean forceFlag){
        warehouseService.assignExistShelfToExistCabinet(shelfId, cabinetId, forceFlag);
        return ResponseEntity.ok().body("Полка "+shelfId+ " успешно перемещена в шкаф "+cabinetId);
    }

    @Operation(summary = "Добавить книгу на полку")
    @PostMapping("/addBookToShelf")
    public ResponseEntity<?> addBookToShelf(int bookId, int shelfId){
        var resp = warehouseService.setBookToBookshelf(bookId, shelfId);
        return resp!=null ? ResponseEntity.ok().body(resp) : ResponseEntity.badRequest().body("Полка не обновлена.");
    }
}
