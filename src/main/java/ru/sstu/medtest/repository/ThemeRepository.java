package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sstu.medtest.entity.Theme;
import ru.sstu.medtest.entity.dto.ThemeDTO;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM THEME_QUESTIONS WHERE QUESTIONS_ID = ?1")
    void removeLinks(Long questionId);

    @Query(value = "SELECT new ru.sstu.medtest.entity.dto.ThemeDTO(id, title, estimatedTime, text, learned) from theme")
    Collection<ThemeDTO> findAllWithoutQuestions();

    @Query(value = "SELECT distinct * from theme where id = ?1", nativeQuery = true)
    Optional<Theme> findByIdD(Long id);

}
