package ru.miit.libe.models;

public enum EUserAccountStatus {
    TEST ("Тестовая учетка"), // Тестовая учетка, вход без двухфакторки
    ACTIVE ("Настоящая учетка"), // Активная учетка, имеет доступ ко входу
    DEACTIVATED ("Отключенная учетка"); // не должна иметь доступ ко входу, но должна занимать место

    private String title;
    EUserAccountStatus(String title){
        this.title = title;
    }
    void setTitle(String title){
        this.title=title;
    }
    String getTitle(){
        return this.title;
    }
}
