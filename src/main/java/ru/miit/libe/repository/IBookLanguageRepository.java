package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.BookLanguage;

import java.util.Optional;

@Repository
public interface IBookLanguageRepository extends JpaRepository<BookLanguage, Integer> {
    boolean existsByLanguageName(String languageName);
}
