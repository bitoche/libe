package ru.miit.libe.models;

public enum EBookStatus {
    IN_STOCK ("В наличии"),
    ISSUED ("Выдана"),
    NOT_AVAILABLE ("Нет в наличии"),
    BOOKED ("Забронирована");

    private String title;
    EBookStatus(String title){
        this.title = title;
    }
    void setTitle(String title){
        this.title=title;
    }
    String getTitle(){
        return this.title;
    }
}
