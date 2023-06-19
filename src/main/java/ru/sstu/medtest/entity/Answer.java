package ru.sstu.medtest.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Answer {
    @Id
    @SequenceGenerator(name = "answer_seq", sequenceName = "answer_squence", initialValue = 201, allocationSize = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_seq")
    @Column(name = "id", nullable = false)
    private Long id;
    private String text;
    private Boolean correct;
}
