package ru.sstu.medtest.entity.results;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.sstu.medtest.entity.QuestionStatus;
import ru.sstu.medtest.entity.Ticket;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TicketAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date lastPass;
    private Integer errorCount;
    @Enumerated(EnumType.STRING)
    private QuestionStatus status;
    @ManyToOne(fetch = FetchType.EAGER)
    private Ticket relatedTicket;
}
