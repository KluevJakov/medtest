package ru.sstu.medtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sstu.medtest.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM TICKET_ANSWER WHERE RELATED_TICKET_ID = ?1")
    void removeLinks(Long ticketId);
}
