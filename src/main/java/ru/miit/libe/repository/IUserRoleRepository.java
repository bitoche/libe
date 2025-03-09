package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.UserRole;

@Repository
@Deprecated
public interface IUserRoleRepository extends JpaRepository<UserRole, Integer> {
// роли Authorized, Teacher, Librarian, Student, Admin, DEACTIVATED
    Boolean existsUserRoleByRoleName(String role_name);
    UserRole getUserRoleByRoleName(String role_name);
}
