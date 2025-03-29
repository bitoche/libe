package ru.miit.libe.models;

import org.springframework.security.core.GrantedAuthority;

public enum EUserRole implements GrantedAuthority {
    DEACTIVATED ("Деактивирован"),
    AUTHORIZED ("Авторизован"),
    STUDENT ("Студент"),
    TEACHER ("Преподаватель"),
    LIBRARIAN ("Библиотекарь"),
    ADMIN ("Администратор"),
    // todo delete
    DEMO ("Роль для демонстрации возможностей приложения");

    private String title;
    EUserRole(String title){
        this.title = title;
    }
    void setTitle(String title){
        this.title=title;
    }
    String getTitle(){
        return this.title;
    }

    @Override
    public String getAuthority() {
        return "ROLE_"+this.name();
    }
    public static EUserRole parseString(String role){
        return switch (role) {
            case "Деактивирован" -> DEACTIVATED;
            case "Авторизован" -> AUTHORIZED;
            case "Студент" -> STUDENT;
            case "Преподаватель" -> TEACHER;
            case "Библиотекарь" -> LIBRARIAN;
            case "Администратор" -> ADMIN;
            default -> null;
        };
    }
}
