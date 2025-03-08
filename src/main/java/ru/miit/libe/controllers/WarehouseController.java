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
@Tag(name = "Управление выдачей книг, заказами, и т.д. // perm::librarian")
@CrossOrigin("http://localhost:3000/")
public class WarehouseController {
    private final WarehouseService warehouseService;
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Operation(summary = "Получить все книжные шкафы")
    @GetMapping("/")
    public ResponseEntity<?> getAllCabinets(){
        return ResponseEntity.ok(warehouseService.getAllCabinets());
    }

    @Operation(summary = "Получить все полки по id шкафа")
    @GetMapping("/{cabinetId}")
    public ResponseEntity<?> getAllShelvesByCabinet(@PathVariable int cabinetId){
        return ResponseEntity.ok(warehouseService.getAllBookshelvesByCabinet(cabinetId));
    }

    @Operation(summary = "Получить все книги по id полки")
    @GetMapping("/s/{shelfId}")
    public ResponseEntity<?> getAllBooksByShelfId(@PathVariable int shelfId){
        return ResponseEntity.ok(warehouseService.getAllBooksByShelfId(shelfId));
    }

    @Operation(summary = "Добавить новый шкаф")
    @PostMapping("/createCabinet")
    public ResponseEntity<?> createNewCabinet(@RequestParam int shelvesCount,
                                              @RequestParam String cabinetName,
                                              @RequestParam int cabinetNumber){
        return ResponseEntity.ok(warehouseService.createNewCabinet(shelvesCount, cabinetName, cabinetNumber));
    }

    @Operation(summary = "Удалить шкаф")
    @PostMapping("/deleteCabinet")
    public ResponseEntity<?> deleteCabinet(@RequestParam int cabinetId,
                                           @RequestParam boolean deleteInnerShelves){
        return ResponseEntity.ok(warehouseService.deleteCabinet(cabinetId, deleteInnerShelves));
    }

    @Operation(summary = "Убрать полку из шкафа (не удаляет полку целиком)")
    @PostMapping("/removeShelfFromCabinet")
    public ResponseEntity<?> removeShelfFromCabinet(@RequestParam int shelfId){
        return ResponseEntity.ok(warehouseService.removeShelfFromCabinet(shelfId));
    }

    @Operation(summary = "Удалить полку целиком")
    @PostMapping("/removeShelfFully")
    public ResponseEntity<?> removeShelfFully(@RequestParam int shelfId){
        return ResponseEntity.ok(warehouseService.deleteShelf(shelfId));
    }
    @Operation(summary = "Получить все полки не в шкафах")
    @GetMapping("/getShelvesWOCabinet")
    public ResponseEntity<?> getShelvesWOCabinet(){
        return ResponseEntity.ok().body(warehouseService.getAllBookshelvesByCabinet(-1));
    }

    @Operation(summary = "Переименовать полку")
    @PostMapping("/renameShelf")
    public ResponseEntity<?> renameShelf(int shelfId, String newName){
        return ResponseEntity.ok().body(warehouseService.renameShelf(shelfId, newName));
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
        return ResponseEntity.ok().body(warehouseService.setBookToBookshelf(bookId, shelfId));
    }

    @Operation(summary = "Убрать книгу с полки")
    @PostMapping("/removeBookFromBookshelf")
    public ResponseEntity<?> removeBookFromShelf(int bookId, int shelfId){
        return ResponseEntity.ok().body(warehouseService.removeBookFromShelf(bookId, shelfId));
    }

    // заказ книг perm:librarian
}
