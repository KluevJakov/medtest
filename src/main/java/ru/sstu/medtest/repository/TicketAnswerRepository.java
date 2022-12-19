package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sstu.medtest.entity.results.TicketAnswer;

@Repository
public interface TicketAnswerRepository extends JpaRepository<TicketAnswer, Long> {
}
