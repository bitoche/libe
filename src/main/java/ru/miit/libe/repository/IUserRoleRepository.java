package ru.miit.libe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.miit.libe.models.UserRole;

@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Integer> {
    Boolean existsUserRoleByRoleName(String role_name);
    UserRole getUserRoleByRoleName(String role_name);
}
