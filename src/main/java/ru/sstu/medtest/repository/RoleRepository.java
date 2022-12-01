package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findBySystemName(String systemName);
}
