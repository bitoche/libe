package ru.miit.libe.services.CRUD;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.miit.libe.models.Book;
import ru.miit.libe.repository.IBookRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CRUDBookService {
    @Autowired
    private final IBookRepository bookRepository;

    public List<Book> getAll(){
        return bookRepository.findAll();
    }
    public Book getById(long id){
        return bookRepository.findById(id).orElse(null);
    }
    public Book getByIdentifier(String identifier){
        return bookRepository.findByIdentifier(identifier).orElse(null);
    }
    public Book saveBook(Book book){
        return bookRepository.save(book);
    }
    public Book deleteBookById(long id){
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.deleteById(id);
                    return book;
                })
                .orElse(null);
    }
}
