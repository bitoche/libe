package ru.miit.libe.cfg;

public class ControllersSecurityConfig {
    public static final String[] LIBRARIAN_ENDPOINTS = {
            "/users/l/**",
            "/librarian/**",
            "/borrows/l/**",
            "/warehouse/**",
            "/notifications/l/**",
            "/reports/**"
    };
    public static final String[] ADMIN_ENDPOINTS = {
            "/admin/**" // функции администратора
    };
    public static final String[] AUTH_ENDPOINTS = {
            "/auth/**" // регистрироваться и авторизоваться
    };

    public static final String[] ACTIVATED_ROLE_ENDPOINTS = {
            "/books/**", // может просматривать каталог
            "/users/**" // может просматривать пользователей
    };

    public static final String[] STUDENT_ENDPOINTS = {
            "/borrows/u/**", // может бронировать книги
            "/notifications/**" // может просматривать уведомления
    };

    public static final String[] TEACHER_ENDPOINTS = {
            "/borrows/t/**" // может запрашивать книги
    };

    public static final String[] RESOURCES_ENDPOINTS = { // доступ к статическим ресурсам сервера
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/index.html", // странички для удобства
            "/login_page.html"
    };
    public static final String[] SWAGGER_ENDPOINTS = { // доступ к сваггеру проекта
            "/swagger-ui/**",
            "/v*/api-docs/**",
            "/swagger-resources/**"
    };
}
