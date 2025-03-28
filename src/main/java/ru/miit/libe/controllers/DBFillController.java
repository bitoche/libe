package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/generation")
@Tag(name = "Заполнение БД тестовыми значениями ")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class DBFillController {
    private final DBFillService dbFillService;
    private final ResponseService rs;

    @Operation(summary = "TEST::TODELETE // Заполнить всю базу тестовыми данными")
    @GetMapping("/todelete/fill")
    public ResponseEntity<?> fill(){
        dbFillService.insertTestLanguages();
        dbFillService.insertTestGenres();
        dbFillService.generateAuthors(50);
        dbFillService.generatePhouses(50);
        dbFillService.generateBooks(50);
        dbFillService.createTestUsers();
        dbFillService.fillWarehouse();
        return rs.build(true);
    }

    @Operation(summary = "TEST::TODELETE // Заполнить books тестовыми данными")
    @GetMapping("/todelete/generateBooks")
    public ResponseEntity<?> generateBooks(){
        return rs.build(dbFillService.generateBooks(20));
    }

    @Operation(summary = "TEST::TODELETE // Заполнить phouses тестовыми данными")
    @GetMapping("/todelete/generatePHouses")
    public ResponseEntity<?> generatePHouses(){
        return rs.build(dbFillService.generatePhouses(20));
    }

    @Operation(summary = "TEST::TODELETE // Заполнить authors тестовыми данными")
    @GetMapping("/todelete/generateAuthors")
    public ResponseEntity<?> generateAuthors(){
        return rs.build(dbFillService.generateAuthors(20));
    }

    @Operation(summary = "TEST::TODELETE // Заполнить genres тестовыми данными")
    @GetMapping("/todelete/insertGenres")
    public ResponseEntity<?> insertGenres(){
        return rs.build(dbFillService.insertTestGenres());
    }

    @Operation(summary = "TEST::TODELETE // Заполнить languages тестовыми данными")
    @GetMapping("/todelete/insertLanguages")
    public ResponseEntity<?> insertLanguages(){
        return rs.build(dbFillService.insertTestLanguages());
    }

    @Operation(summary = "TEST::TODELETE // Заполнить склад тестовыми данными (РАБОТАЕТ ТОЛЬКО НА ПЕРВЫЙ РАЗ)")
    @GetMapping("/todelete/fillWarehouse")
    public ResponseEntity<?> fillWarehouse(){
        dbFillService.fillWarehouse();
        return rs.build(true);
    }

    @Operation(summary = "TEST::TODELETE // Заполнить users тестовыми данными (password = email) ")
    @GetMapping("/todelete/fillUsers")
    public ResponseEntity<?> fillUsers(){
        dbFillService.createTestUsers();
        return rs.build(true);
    }
}
