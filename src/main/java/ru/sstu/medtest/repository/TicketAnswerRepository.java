package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sstu.medtest.entity.results.TicketAnswer;

@Repository
public interface TicketAnswerRepository extends JpaRepository<TicketAnswer, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM USER_ENTITY_TICKETS_ANSWERS WHERE TICKETS_ANSWERS_ID IN (SELECT ID FROM TICKET_ANSWER WHERE RELATED_TICKET_ID = ?1)")
    void removeLinks(Long ticketId);
}
