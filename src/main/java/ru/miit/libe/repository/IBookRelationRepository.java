package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.BookRelation;

@Repository
public interface IBookRelationRepository extends JpaRepository<BookRelation, Long> {
}
