package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.PublishingHouse;

import java.util.Optional;

@Repository
public interface IPublishingHouseRepository extends JpaRepository<PublishingHouse, Long> {
    boolean existsByPublishingHouseName(String publishingHouseName);
    Optional<PublishingHouse> findByPublishingHouseName(String publishingHouseName);
}
