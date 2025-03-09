package ru.miit.libe.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.miit.libe.models.*;
import ru.miit.libe.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class DBFillService {
    @Autowired
    IBookRepository bookRepository;
    @Autowired
    IBookGenreRepository bookGenreRepository;
    @Autowired
    IPublishingHouseRepository publishingHouseRepository;
    @Autowired
    IBookAuthorRepository bookAuthorRepository;
    @Autowired
    IBookLanguageRepository bookLanguageRepository;

    @Transactional
    public List<Book> generateBooks(int count) {
        List<Book> generated = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Book b = Book.generateWOIdAndDependencies();
            if(bookAuthorRepository.findAll().isEmpty() || bookGenreRepository.findAll().isEmpty() || publishingHouseRepository.findAll().isEmpty() || bookGenreRepository.findAll().isEmpty() || bookLanguageRepository.findAll().isEmpty()){
                System.out.println("DB is not filled");
                return null;
            }
            b.setAuthors(RandomService.selectRandObjectsFrom(bookAuthorRepository.findAll(), 1));
            b.setGenres(RandomService.selectRandObjectsFrom(bookGenreRepository.findAll(), 1));
            b.setPublishingHouse(RandomService.selectRandObjectsFrom(publishingHouseRepository.findAll(), 1).getFirst());
            b.setLanguage(RandomService.selectRandObjectsFrom(bookLanguageRepository.findAll(), 1).getFirst());
            bookRepository.save(b);
            generated.add(b);
        }
        return generated;
    }

    public List<PublishingHouse> generatePhouses(int count) {
        List<PublishingHouse> generated = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PublishingHouse p = new PublishingHouse();
            p.setPublishingHouseName(RandomService.generateRandomPhrase());
            publishingHouseRepository.save(p);
            generated.add(p);
        }
        return generated;
    }

    public List<BookAuthor> generateAuthors(int count) {
        List<BookAuthor> generated = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BookAuthor p = RandomService.randBetween(1,2)==1 ?
                    BookAuthor.generateFemaleWOId() :
                    BookAuthor.generateMaleWOId();
            bookAuthorRepository.save(p);
            generated.add(p);
        }
        return generated;
    }

    public List<BookGenre> insertTestGenres() {
        List<BookGenre> inserted = new ArrayList<>();
        List<String> genres = List.of(
                "Классика", "Ужасы", "Фантастика", "Фентези", "Романтика", "Приключения",
                "Боевик", "Детектив", "Комедия", "Эротика", "Вымышленная автобиография", "Автобиография",
                "Каминаут"
        );
        for (String genreName : genres){
            BookGenre g = new BookGenre();
            g.setGenreName(genreName);
            bookGenreRepository.save(g);
            inserted.add(g);
        }
        return inserted;
    }

    public List<BookLanguage> insertTestLanguages() {
        List<BookLanguage> inserted = new ArrayList<>();
        List<String> languages = List.of(
                "Русский",         // Русский
                "English",         // Английский
                "Español",         // Испанский
                "Français",        // Французский
                "Deutsch",         // Немецкий
                "中文",             // Китайский (упрощённый)
                "日本語",           // Японский
                "Italiano",        // Итальянский
                "Português",       // Португальский
                "한국어",           // Корейский
                "العربية",         // Арабский
                "हिन्दी",           // Хинди
                "Türkçe",          // Турецкий
                "Nederlands",      // Нидерландский
                "Polski",          // Польский
                "Svenska",         // Шведский
                "Suomi",           // Финский
                "Čeština",         // Чешский
                "Ελληνικά",        // Греческий
                "Українська"       // Украинский
        );
        for (String lang : languages){
            BookLanguage l = new BookLanguage();
            l.setLanguageName(lang);
            bookLanguageRepository.save(l);
            inserted.add(l);
        }
        return inserted;
    }
}
