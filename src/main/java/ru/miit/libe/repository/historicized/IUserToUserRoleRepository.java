package ru.miit.libe.repository.historicized;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.historicized.UserToUserRole;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IUserToUserRoleRepository extends JpaRepository<UserToUserRole, Long> {
    List<UserToUserRole> getAllByAssignDttmBetween(LocalDateTime assignDttmStart, LocalDateTime assignDttmEnd);
}
