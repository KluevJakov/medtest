package ru.sstu.medtest.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Question {
    @Id
    @SequenceGenerator(name = "q_seq", sequenceName = "q_squence", initialValue = 200, allocationSize = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "q_seq")
    @Column(name = "id", nullable = false)
    private Long id;
    private String text;
    private Boolean favorite;
    @Enumerated(EnumType.STRING)
    private QuestionStatus status;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Answer> answers;
}
