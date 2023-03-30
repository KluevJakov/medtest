package ru.sstu.medtest.entity.results;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.sstu.medtest.entity.Question;
import ru.sstu.medtest.entity.QuestionStatus;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean favorite;
    @Enumerated(EnumType.STRING)
    private QuestionStatus status;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST})
    private Question relatedQuestion;
}
