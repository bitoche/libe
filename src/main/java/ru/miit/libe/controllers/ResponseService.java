package ru.miit.libe.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {
    public ResponseEntity<?> build(Object object) {
        if (object == null) {
            // Если объект null, возвращаем BAD_REQUEST с сообщением
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"response\": \"null\"}");
        } else if (object instanceof List<?>) {
            // Если объект является списком, проверяем, не пустой ли он
            List<?> list = (List<?>) object;
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"response\": \"empty list\"}");
            } else {
                return ResponseEntity.ok(object);
            }
        } else if (object instanceof String) {
            // Если объект является строкой, проверяем, не пустая ли она
            String str = (String) object;
            if (str.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"response\": \"empty string\"}");
            } else {
                return ResponseEntity.ok(object);
            }
        } else {
            // Для всех остальных случаев возвращаем объект с OK
            return ResponseEntity.ok(object);
        }
    }
}
