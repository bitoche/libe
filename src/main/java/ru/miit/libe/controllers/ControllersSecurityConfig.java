package ru.miit.libe.controllers;

import org.apache.commons.lang3.ArrayUtils;

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
            "/admin/**"
    };
    public static final String[] DEACTIVATED_ROLE_ENDPOINTS = {
            "/auth/**" // регистрироваться и авторизоваться
    };

    static String[] activated_only = {
            "/books/**", // может просматривать каталог
            "/users/**" // может просматривать пользователей
    };
    public static final String[] ACTIVATED_ROLE_ENDPOINTS = ArrayUtils.addAll(
            DEACTIVATED_ROLE_ENDPOINTS, activated_only
    );

    static String[] student_only = {
            "/borrows/u/**", // может бронировать книги
            "/notifications/**" // может просматривать уведомления
    };
    public static final String[] STUDENT_ENDPOINTS =  ArrayUtils.addAll(
            ACTIVATED_ROLE_ENDPOINTS, student_only
    );

    static String[] teacher_only = {
            "/borrows/t/**" // может запрашивать книги
    };
    public static final String[] TEACHER_ENDPOINTS = ArrayUtils.addAll(
            STUDENT_ENDPOINTS, teacher_only
    );
}
