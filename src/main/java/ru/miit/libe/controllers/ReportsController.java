package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.models.EBookStatus;
import ru.miit.libe.models.historicized.EReportType;
import ru.miit.libe.services.ReportsService;

import java.sql.Date;

@Controller
@RestController
@RequestMapping("/reports")
@Tag(name = "Управление отчетами // perm:librarian")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
@AllArgsConstructor
public class ReportsController {
    private final ReportsService reportsService;
    private final ResponseService rs;

    // получить отчеты

    // сгенерировать отчеты
    @GetMapping("/generate/by/period")
    @Operation(summary = "Сгенерировать отчеты по данным за период")
    public ResponseEntity<?> generateByStatus(@RequestParam Date startDate,
                                              @RequestParam Date endDate){
        return rs.build(reportsService.startReports(startDate, endDate, EReportType.ALL));
    }
    // отчет по заказам книг
    // количество книг на складе

}
