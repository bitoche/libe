package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.Cabinet;

import java.util.List;

@Repository
public interface ICabinetRepository extends JpaRepository<Cabinet, Integer> {
    List<Cabinet> findByCabinetNameContainsIgnoreCase(String cabinetName);
}
