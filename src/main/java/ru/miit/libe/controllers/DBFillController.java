package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.miit.libe.services.DBFillService;


@Controller
@RestController
@RequestMapping("/api/generation")
@Tag(name = "Заполнение БД тестовыми значениями ")
@CrossOrigin("http://localhost:3000/")
@AllArgsConstructor
public class DBFillController {
    private final DBFillService dbFillService;
    private final ResponseService rs;
    @Operation(summary = "TEST::TODELETE // Заполнить books тестовыми данными")
    @GetMapping("/generateBooks")
    public ResponseEntity<?> generateBooks(){
        return rs.build(dbFillService.generateBooks(20));
    }

    @Operation(summary = "TEST::TODELETE // Заполнить phouses тестовыми данными")
    @GetMapping("/generatePHouses")
    public ResponseEntity<?> generatePHouses(){
        return rs.build(dbFillService.generatePhouses(20));
    }

    @Operation(summary = "TEST::TODELETE // Заполнить authors тестовыми данными")
    @GetMapping("/generateAuthors")
    public ResponseEntity<?> generateAuthors(){
        return rs.build(dbFillService.generateAuthors(20));
    }

    @Operation(summary = "TEST::TODELETE // Заполнить genres тестовыми данными")
    @GetMapping("/insertGenres")
    public ResponseEntity<?> insertGenres(){
        return rs.build(dbFillService.insertTestGenres());
    }

    @Operation(summary = "TEST::TODELETE // Заполнить languages тестовыми данными")
    @GetMapping("/insertLanguages")
    public ResponseEntity<?> insertLanguages(){
        return rs.build(dbFillService.insertTestLanguages());
    }

    @Operation(summary = "TEST::TODELETE // Заполнить всю базу тестовыми данными")
    @GetMapping("/fill")
    public ResponseEntity<?> fill(){
        dbFillService.insertTestLanguages();
        dbFillService.insertTestGenres();
        dbFillService.generateAuthors(20);
        dbFillService.generatePhouses(20);
        dbFillService.generateBooks(20);
        dbFillService.createTestUsers();
        dbFillService.fillWarehouse();
        return rs.build(true);
    }

    @Operation(summary = "TEST::TODELETE // Заполнить склад тестовыми данными")
    @GetMapping("/fillWarehouse")
    public ResponseEntity<?> fillWarehouse(){
        dbFillService.fillWarehouse();
        return rs.build(true);
    }
}
