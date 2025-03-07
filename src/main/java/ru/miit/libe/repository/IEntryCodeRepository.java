package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.EntryCode;
import ru.miit.libe.models.User;

@Repository
public interface IEntryCodeRepository extends JpaRepository<EntryCode, Long> {
    EntryCode getEntryCodeByUser_UserId(Long userId);
    boolean existsByUser(User user);

    EntryCode findByUserAndCode(User user, String code);

    void deleteByUser(User user);
}
