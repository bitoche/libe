package ru.miit.libe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import ru.miit.libe.models.EBookStatus;
import ru.miit.libe.models.historicized.EReportType;
import ru.miit.libe.services.ReportsService;

import java.net.UnknownHostException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/reports")
@Tag(name = "Управление отчетами // perm:librarian")
@CrossOrigin({"http://localhost:3000/", "https://bitoche.cloudpub.ru/"})
@AllArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ReportsController {
    private final ReportsService reportsService;
    private final ResponseService rs;

    // получить список отчетов
    @GetMapping("/get/list")
    @Operation(summary = "Получить список сформированных отчетов")
    public ResponseEntity<?> getReportsList(){
        return rs.build(reportsService.getReportsList());
    }


    // получить отчеты
    @GetMapping("/get/by_id/{calcId}")
    @Operation(summary = "Получить отчет по его ID и типу")
    public ResponseEntity<?> getReportByCalcId(@PathVariable int calcId, @RequestParam @Nullable EReportType reportType){
        return rs.build(reportsService.getReportById(calcId, reportType));
    }

    // сгенерировать отчеты
    @PostMapping("/generate/by/period")
    @Operation(summary = "Сгенерировать отчеты по данным за период")
    public ResponseEntity<?> generateByStatus(@RequestParam Date startDate,
                                              @RequestParam Date endDate) {
        var response = reportsService.startReports(startDate, endDate, EReportType.ALL);
        try{
            var data = response.getBody();
            assert data != null;
            Object calcId = ((Map<String, Object>) data).get("calc_id");
            var resp = new HashMap<>();
            resp.put("status", "success");
            resp.put("calc_id", calcId);
            return rs.build(resp);
        }
        catch (RuntimeException e){
            System.out.println("exception while parsing response from python reports service");
            var resp = new HashMap<>();
            resp.put("status", "error");
            resp.put("message", e);
            return rs.build(resp);
        }
    }


}
