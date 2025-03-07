package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.BookToBookRelation;

@Repository
public interface IBookToBookRelationRepository extends JpaRepository<BookToBookRelation, Integer> {
}
