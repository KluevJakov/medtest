package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sstu.medtest.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM TICKET_QUESTIONS WHERE QUESTIONS_ID = ?1")
    void removeLinks(Long questionId);
}
