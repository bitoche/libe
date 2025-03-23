package ru.miit.libe.models.historicized;

public enum EReportType {
    BOOK_TO_BOOK_STATUS ("Изменения статусов книг"),
    ALL ("Все возможные отчеты");


    private String title;
    EReportType(String title){
        this.title = title;
    }
    void setTitle(String title){
        this.title=title;
    }
    String getTitle(){
        return this.title;
    }
}
