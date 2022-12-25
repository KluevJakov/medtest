package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.Exam;
import ru.sstu.medtest.entity.UserEntity;

import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByUserAttempt(UserEntity userEntity);
}
