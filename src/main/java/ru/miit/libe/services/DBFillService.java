package ru.miit.libe.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.miit.libe.models.*;
import ru.miit.libe.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired
    IUserRepository userRepository;
    @Autowired
    ICabinetRepository cabinetRepo;
    @Autowired
    IBookShelfRepository bookShelfRepository;

    @Autowired
    private ReportsService reportsService;


    public List<Book> generateBooks(int count) {
        List<Book> generated = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Book b = Book.generateWOIdAndDependencies();
            if(bookAuthorRepository.findAll().isEmpty() || bookGenreRepository.findAll().isEmpty() || publishingHouseRepository.findAll().isEmpty() || bookGenreRepository.findAll().isEmpty() || bookLanguageRepository.findAll().isEmpty()){
                System.out.println("DB is not filled");
                return null;
            }
            b.setAuthors(RandomService.selectRandObjectsFrom(bookAuthorRepository.findAll(), 2));
            b.setGenres(RandomService.selectRandObjectsFrom(bookGenreRepository.findAll(), 2));
            b.setPublishingHouse(RandomService.selectRandObjectsFrom(publishingHouseRepository.findAll(), 1).getFirst());
            b.setLanguage(RandomService.selectRandObjectsFrom(bookLanguageRepository.findAll(), 1).getFirst());
            bookRepository.save(b);
            reportsService.addBookToBookStatusAssign(b, b.getBookStatus());
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
        if (bookGenreRepository.findAll().isEmpty()){
            for (String genreName : genres){
                BookGenre g = new BookGenre();
                g.setGenreName(genreName);
                bookGenreRepository.save(g);
                inserted.add(g);
            }
            return inserted;
        }
        return null;
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
        if(bookLanguageRepository.findAll().isEmpty()){
            for (String lang : languages){
                BookLanguage l = new BookLanguage();
                l.setLanguageName(lang);
                bookLanguageRepository.save(l);
                inserted.add(l);
            }
            return inserted;
        }
        return null;
    }

    public void createTestUsers() {
        long maxId = userRepository.findAll().stream()
                .mapToLong(User::getUserId) // Преобразуем пользователей в их id (тип long)
                .max() // Находим максимальное значение
                .orElse(0);
        for (int i = 0; i < Arrays.stream(EUserRole.values()).count(); i++) {
            var user = new User();
            user.setRole(EUserRole.values()[i]);
            long currId = i+maxId;
            user.setEmail("testemail"+currId+"@test"+currId+".test");
            PasswordEncoder pe = new BCryptPasswordEncoder();
            user.setPassword(pe.encode(user.getEmail()));
            user.setBirthDate(RandomService.generateRandomDate(50));
            user.setFirstName(RandomService.randFrom(RandomService.MALE_NAMES));
            user.setSecondName(RandomService.randFrom(RandomService.MALE_SURNAMES));
            user.setThirdName(RandomService.randFrom(RandomService.MALE_PATRONYMICS));
            user.setRegisterDttm(LocalDateTime.now());
            userRepository.save(user);
            reportsService.addUserToUserRoleAssign(user, user.getRole());
        }

    }

    public void fillWarehouse() {
        var ab = bookRepository.findAll();
        if (!ab.isEmpty()) {
            int booksForShelf = 3; // Книг на одной полке
            int shelvesForCabinet = 3; // Полок в одном шкафу
            int totalBooksPerCabinet = booksForShelf * shelvesForCabinet; // Всего книг в одном шкафу
            int cabinetsCount = (int) Math.ceil((double) ab.size() / totalBooksPerCabinet); // Количество шкафов

            int bookIndex = 0; // Индекс текущей книги

            for (int i = 0; i < cabinetsCount; i++) {
                Cabinet c = new Cabinet();
                c.setCabinetNumber(i);
                c.setCabinetName("Шкаф №" + i);
                cabinetRepo.save(c);
                c.setShelves(new ArrayList<>());

                for (int j = 0; j < shelvesForCabinet; j++) {
                    Bookshelf bs = new Bookshelf();
                    bs.setCabinet(c);
                    bs.setShelfName("Полка №" + j);
                    bs.setBooks(new ArrayList<>());

                    for (int k = 0; k < booksForShelf; k++) {
                        if (bookIndex < ab.size()) { // Проверяем, чтобы не выйти за пределы списка
                            bs.addBook(ab.get(bookIndex));
                            bookIndex++; // Переходим к следующей книге
                        } else {
                            break; // Если книги закончились, выходим из цикла
                        }
                    }

                    bookShelfRepository.save(bs);
                    c.getShelves().add(bs); // Добавляем полку в шкаф
                }

                cabinetRepo.save(c); // Сохраняем шкаф с полками

            }
            // для теста отчетов
            for (Bookshelf bs : bookShelfRepository.findAll()) {
                assert bs.getBooks() != null;
                for (Book b : bs.getBooks()){
                    reportsService.addBookToBookshelfAssign(b, bs, "+");
                }
            }
        }
    }
}
