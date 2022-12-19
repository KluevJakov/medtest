package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.results.QuestionAnswer;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
}
