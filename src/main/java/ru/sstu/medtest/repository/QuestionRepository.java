package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
