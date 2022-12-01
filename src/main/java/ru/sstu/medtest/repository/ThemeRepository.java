package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.Theme;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
}
