package ru.sstu.medtest.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Ticket {
    @Id
    @SequenceGenerator(name = "ticker_seq", sequenceName = "ticker_squence", initialValue = 50, allocationSize = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticker_seq")
    @Column(name = "id", nullable = false)
    private Long id;
    private Date lastPass;
    private Integer errorCount;
    @Enumerated(EnumType.STRING)
    private QuestionStatus status;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Question> questions;
}
