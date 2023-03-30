package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sstu.medtest.entity.results.QuestionAnswer;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM QUESTION_ANSWERS WHERE ANSWERS_ID = ?1")
    void removeLinks(Long answerId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM USER_ENTITY_QUESTIONS_ANSWERS WHERE QUESTIONS_ANSWERS_ID IN (SELECT ID FROM QUESTION_ANSWER WHERE RELATED_QUESTION_ID = ?1)")
    void removeLinks1(Long questionId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM QUESTION_ANSWER WHERE RELATED_QUESTION_ID = ?1")
    void removeLinks2(Long questionId);
}
