package ru.miit.libe.models;

public enum EBorrowStatus {
    ON_HANDS ("На руках"),
    RETURNED ("Возвращена"),
    AWAITING_RECIEPT ("Ожидает получения");


    private String title;
    EBorrowStatus(String title){
        this.title = title;
    }
    void setTitle(String title){
        this.title=title;
    }
    String getTitle(){
        return this.title;
    }
}
