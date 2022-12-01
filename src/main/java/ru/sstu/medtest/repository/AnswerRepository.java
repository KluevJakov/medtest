package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
