package ru.miit.libe.models.historicized;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum EReportType {
    //BOOK_TO_BOOK_STATUS ("Изменения статусов книг"),
    ALL ("Все возможные отчеты", "all"),
    READABILITY ("Витрина \"Читаемость\"", "rep_readability"),
    APPEAL_RATE ("Витрина \"Обращаемость\"", "rep_appeal_rate"),
    METRICS ("Основные показатели", "rep_metrics");
    @Setter
    private String title;
    @Setter
    private String code;
//    EReportType(String title, String code){
//        this.title = title;
//        this.code = code;
//    }


}
